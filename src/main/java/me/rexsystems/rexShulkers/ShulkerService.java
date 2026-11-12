package me.rexsystems.rexShulkers;

import me.rexsystems.rexShulkers.config.ConfigManager;
import me.rexsystems.rexShulkers.gui.ShulkerHolder;
import me.rexsystems.rexShulkers.item.ShulkerItems;
import me.rexsystems.rexShulkers.lock.OpenLock;
import me.rexsystems.rexShulkers.session.Cooldown;
import me.rexsystems.rexShulkers.session.OpenSession;
import me.rexsystems.rexShulkers.storage.ContentCodec;
import me.rexsystems.rexShulkers.storage.ShulkerRepository;
import me.rexsystems.rexShulkers.util.MessageService;
import me.rexsystems.rexShulkers.util.Placeholders;
import me.rexsystems.rexShulkers.util.SchedulerUtils;
import me.rexsystems.rexShulkers.util.TextParser;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ShulkerService {

    private final RexShulkers plugin;
    private final ShulkerItems items;
    private final ShulkerRepository repository;
    private final OpenLock lock = new OpenLock();
    private final Cooldown cooldown = new Cooldown();
    private final ConcurrentHashMap<UUID, OpenSession> byStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, UUID> storageByPlayer = new ConcurrentHashMap<>();

    public ShulkerService(RexShulkers plugin, ShulkerItems items, ShulkerRepository repository) {
        this.plugin = plugin;
        this.items = items;
        this.repository = repository;
    }

    public OpenLock lock() {
        return lock;
    }

    public ShulkerItems items() {
        return items;
    }

    public boolean hasOpen(UUID playerId) {
        return playerId != null && storageByPlayer.containsKey(playerId);
    }

    public long cooldownRemainingSeconds(UUID playerId) {
        long millis = cooldown.remainingMillis(playerId, config().cooldownSeconds() * 1000L, System.currentTimeMillis());
        if (millis <= 0L) {
            return 0L;
        }
        return (millis + 999L) / 1000L;
    }

    public boolean isLocked(ItemStack stack) {
        UUID id = items.read(stack);
        return id != null && lock.isOpen(id);
    }

    public boolean isBlacklisted(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        return config().blacklist().blocked(stack.getType().name());
    }

    public void openFromHand(Player player, EquipmentSlot hand) {
        ItemStack stack = stackIn(player, hand);
        if (items.read(stack) == null) {
            return;
        }
        if (config().obfuscate()) {
            items.clearBlockInventory(stack);
            putStack(player, hand, stack);
        }
        open(player, items.read(stackIn(player, hand)));
    }

    public void openCommand(Player player) {
        ItemStack main = player.getInventory().getItemInMainHand();
        if (items.read(main) != null) {
            openFromHand(player, EquipmentSlot.HAND);
            return;
        }
        ItemStack off = player.getInventory().getItemInOffHand();
        if (items.read(off) != null) {
            openFromHand(player, EquipmentSlot.OFF_HAND);
            return;
        }
        send(player, "not-holding", Map.of());
    }

    public void give(CommandSender sender, Player target, int amount) {
        SchedulerUtils.runForPlayer(plugin, target, () -> giveNow(sender, target, amount));
    }

    public void clear(Player player) {
        EquipmentSlot hand = EquipmentSlot.HAND;
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (items.read(stack) == null) {
            stack = player.getInventory().getItemInOffHand();
            hand = EquipmentSlot.OFF_HAND;
        }
        UUID id = items.read(stack);
        if (id == null) {
            send(player, "clear-missing", Map.of());
            return;
        }
        if (lock.isOpen(id)) {
            send(player, "clear-open", Map.of());
            return;
        }
        UUID removed = items.clear(stack);
        if (removed == null) {
            send(player, "clear-missing", Map.of());
            return;
        }
        putStack(player, hand, stack);
        try {
            repository.delete(removed);
        } catch (RuntimeException ex) {
            plugin.getLogger().warning("Could not delete shulker " + removed + ": " + ex.getMessage());
        }
        send(player, "clear-done", Map.of());
    }

    public void snapshot(Inventory inventory) {
        OpenSession session = session(inventory);
        if (session == null) {
            return;
        }
        session.update(ContentCodec.copy(inventory.getContents()));
    }

    public void closeInventory(Inventory inventory, boolean playSound) {
        OpenSession session = session(inventory);
        if (session == null) {
            return;
        }
        saveSession(session, ContentCodec.copy(inventory.getContents()), playSound, false);
    }

    public void savePlayer(Player player, boolean playSound) {
        if (player == null) {
            return;
        }
        UUID storageId = storageByPlayer.get(player.getUniqueId());
        if (storageId == null) {
            return;
        }
        OpenSession session = byStorage.get(storageId);
        if (session == null) {
            return;
        }
        ItemStack[] contents = ContentCodec.copy(session.inventory().getContents());
        saveSession(session, contents, playSound, true);
    }

    public void saveAll() {
        for (OpenSession session : List.copyOf(byStorage.values())) {
            saveSession(session, ContentCodec.copy(session.latest()), false, false);
        }
    }

    public void shutdown() {
        saveAll();
        repository.close();
    }

    private void open(Player player, UUID id) {
        if (id == null) {
            return;
        }
        if (!player.hasPermission("rexshulkers.use")) {
            send(player, "no-permission", Map.of());
            return;
        }
        if (!config().worldAllowed(player.getWorld().getName())) {
            send(player, "world-denied", Map.of());
            return;
        }
        if (!player.hasPermission("rexshulkers.admin")) {
            long left = cooldown.remainingMillis(player.getUniqueId(), config().cooldownSeconds() * 1000L, System.currentTimeMillis());
            if (left > 0L) {
                send(player, "cooldown", Map.of("seconds", Long.toString(Math.max(1L, (left + 999L) / 1000L))));
                return;
            }
        }
        if (!lock.tryOpen(id, player.getUniqueId())) {
            send(player, "already-open", Map.of());
            return;
        }

        ItemStack[] stored;
        try {
            stored = repository.load(id);
        } catch (RuntimeException ex) {
            lock.release(id);
            plugin.getLogger().warning("Could not load shulker " + id + ": " + ex.getMessage());
            send(player, "storage-error", Map.of());
            return;
        }

        int slots = config().slots();
        if (stored.length > slots) {
            lock.release(id);
            send(player, "slots-too-small", Map.of(
                    "saved", Integer.toString(stored.length),
                    "slots", Integer.toString(slots)));
            return;
        }

        ShulkerHolder holder = new ShulkerHolder(id, player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(holder, slots, TextParser.parse(config().guiTitle()));
        holder.attach(inventory);
        ItemStack[] fitted = new ItemStack[slots];
        System.arraycopy(stored, 0, fitted, 0, stored.length);
        inventory.setContents(fitted);

        OpenSession session = new OpenSession(id, player.getUniqueId(), inventory);
        session.update(ContentCodec.copy(fitted));
        byStorage.put(id, session);
        storageByPlayer.put(player.getUniqueId(), id);
        try {
            player.openInventory(inventory);
        } catch (RuntimeException ex) {
            byStorage.remove(id, session);
            storageByPlayer.remove(player.getUniqueId(), id);
            lock.release(id);
            throw ex;
        }
        play(player, "open");
    }

    private void saveSession(OpenSession session, ItemStack[] contents, boolean playSound, boolean closeInventory) {
        if (!session.markSaved()) {
            return;
        }
        try {
            ItemStack[] copy = ContentCodec.copy(contents);
            session.update(copy);
            try {
                repository.save(session.storageId(), copy);
            } catch (RuntimeException ex) {
                plugin.getLogger().severe("Could not save shulker " + session.storageId() + ": " + ex.getMessage());
            }
            if (closeInventory) {
                Player player = Bukkit.getPlayer(session.playerId());
                if (player != null && player.isOnline()
                        && player.getOpenInventory().getTopInventory() == session.inventory()) {
                    try {
                        player.closeInventory();
                    } catch (RuntimeException ex) {
                        plugin.getLogger().warning("Could not close shulker for " + player.getName() + ": " + ex.getMessage());
                    }
                }
            }
        } finally {
            byStorage.remove(session.storageId(), session);
            storageByPlayer.remove(session.playerId(), session.storageId());
            lock.release(session.storageId());
            cooldown.mark(session.playerId(), System.currentTimeMillis());
            if (playSound) {
                Player player = Bukkit.getPlayer(session.playerId());
                if (player != null && player.isOnline()) {
                    play(player, "close");
                }
            }
        }
    }

    private void giveNow(CommandSender sender, Player target, int amount) {
        if (!target.isOnline()) {
            send(sender, "unknown-player", Map.of());
            return;
        }
        ConfigManager settings = config();
        int dropped = 0;
        for (int i = 0; i < amount; i++) {
            ItemStack stack = items.create(UUID.randomUUID(), settings.itemName(), settings.itemLore());
            HashMap<Integer, ItemStack> leftover = target.getInventory().addItem(stack);
            for (ItemStack left : leftover.values()) {
                dropped += left.getAmount();
                target.getWorld().dropItemNaturally(target.getLocation(), left);
            }
        }
        Map<String, String> placeholders = Map.of(
                "amount", Integer.toString(amount),
                "player", target.getName(),
                "dropped", Integer.toString(dropped));
        send(sender, "given", placeholders);
        if (dropped > 0) {
            send(sender, "dropped", placeholders);
        }
        if (!sender.equals(target)) {
            send(target, "received", placeholders);
        }
    }

    private OpenSession session(Inventory inventory) {
        if (inventory == null || !(inventory.getHolder() instanceof ShulkerHolder holder)) {
            return null;
        }
        OpenSession session = byStorage.get(holder.storageId());
        if (session == null || session.inventory() != inventory) {
            return null;
        }
        return session;
    }

    private void play(Player player, String which) {
        if (!config().soundEnabled(which)) {
            return;
        }
        String sound = config().soundName(which);
        if (sound.isBlank()) {
            return;
        }
        try {
            player.playSound(player.getLocation(), sound, SoundCategory.BLOCKS,
                    config().soundVolume(which), config().soundPitch(which));
        } catch (RuntimeException ex) {
            plugin.getLogger().warning("Could not play sound " + sound + ": " + ex.getMessage());
        }
    }

    private void send(CommandSender sender, String path, Map<String, String> placeholders) {
        String raw = config().prefixed(path);
        if (sender instanceof Player player) {
            raw = Placeholders.apply(player, raw);
        }
        MessageService.send(sender, raw, placeholders);
    }

    public void sendConfigured(CommandSender sender, String path) {
        send(sender, path, Map.of());
    }

    public void sendHelp(CommandSender sender) {
        send(sender, "help-header", Map.of());
        for (String line : config().helpLines()) {
            String raw = line;
            if (sender instanceof Player player) {
                raw = Placeholders.apply(player, raw);
            }
            MessageService.send(sender, raw, Map.of());
        }
    }

    private ConfigManager config() {
        return plugin.getConfigManager();
    }

    private static ItemStack stackIn(Player player, EquipmentSlot hand) {
        if (hand == EquipmentSlot.OFF_HAND) {
            return player.getInventory().getItemInOffHand();
        }
        return player.getInventory().getItemInMainHand();
    }

    private static void putStack(Player player, EquipmentSlot hand, ItemStack stack) {
        if (hand == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(stack);
            return;
        }
        player.getInventory().setItemInMainHand(stack);
    }
}
