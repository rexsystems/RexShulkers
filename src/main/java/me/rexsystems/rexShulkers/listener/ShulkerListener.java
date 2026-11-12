package me.rexsystems.rexShulkers.listener;

import me.rexsystems.rexShulkers.RexShulkers;
import me.rexsystems.rexShulkers.gui.ShulkerHolder;
import me.rexsystems.rexShulkers.lock.ClickGuard;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class ShulkerListener implements Listener {

    private final RexShulkers plugin;

    public ShulkerListener(RexShulkers plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        EquipmentSlot hand = event.getHand();
        if (hand == null) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack stack = hand == EquipmentSlot.OFF_HAND
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();
        if (plugin.getShulkers().items().read(stack) == null) {
            return;
        }
        denyUse(event);
        if (hand == EquipmentSlot.OFF_HAND
                && plugin.getShulkers().items().read(player.getInventory().getItemInMainHand()) != null) {
            if (plugin.getConfigManager().obfuscate()) {
                plugin.getShulkers().items().clearBlockInventory(stack);
                player.getInventory().setItemInOffHand(stack);
            }
            return;
        }
        plugin.getShulkers().openFromHand(player, hand);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.getShulkers().items().read(event.getItemInHand()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.getShulkers().isLocked(event.getItemDrop().getItemStack())) {
            return;
        }
        event.setCancelled(true);
        plugin.getShulkers().sendConfigured(event.getPlayer(), "locked");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (!plugin.getShulkers().isLocked(event.getMainHandItem())
                && !plugin.getShulkers().isLocked(event.getOffHandItem())) {
            return;
        }
        event.setCancelled(true);
        plugin.getShulkers().sendConfigured(event.getPlayer(), "locked");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int topSize = event.getView().getTopInventory().getSize();
        boolean ourGui = event.getView().getTopInventory().getHolder() instanceof ShulkerHolder;
        ItemStack incoming = incoming(event, topSize);
        boolean insert = ourGui && incoming != null && !incoming.getType().isAir();
        boolean blacklisted = insert && plugin.getShulkers().isBlacklisted(incoming);
        boolean locked = involvesLocked(event);
        if (!ClickGuard.cancel(insert, blacklisted, locked)) {
            return;
        }
        event.setCancelled(true);
        if (blacklisted && !locked) {
            plugin.getShulkers().sendConfigured(player, "blacklisted");
            return;
        }
        plugin.getShulkers().sendConfigured(player, "locked");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onClickSnapshot(InventoryClickEvent event) {
        plugin.getShulkers().snapshot(event.getView().getTopInventory());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        boolean locked = plugin.getShulkers().isLocked(event.getOldCursor());
        boolean ourGui = event.getView().getTopInventory().getHolder() instanceof ShulkerHolder;
        int topSize = event.getView().getTopInventory().getSize();
        boolean touchesTop = event.getRawSlots().stream().anyMatch(slot -> slot < topSize);
        boolean blacklisted = ourGui && touchesTop && plugin.getShulkers().isBlacklisted(event.getOldCursor());
        if (!ClickGuard.cancel(blacklisted, blacklisted, locked)) {
            return;
        }
        event.setCancelled(true);
        if (blacklisted && !locked) {
            plugin.getShulkers().sendConfigured(player, "blacklisted");
            return;
        }
        plugin.getShulkers().sendConfigured(player, "locked");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onDragSnapshot(InventoryDragEvent event) {
        plugin.getShulkers().snapshot(event.getView().getTopInventory());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onClose(InventoryCloseEvent event) {
        plugin.getShulkers().closeInventory(event.getInventory(), true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getShulkers().savePlayer(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        plugin.getShulkers().savePlayer(event.getEntity(), true);
    }

    private ItemStack incoming(InventoryClickEvent event, int topSize) {
        ClickType click = event.getClick();
        int raw = event.getRawSlot();
        boolean inTop = raw >= 0 && raw < topSize;
        if (click.isShiftClick()) {
            if (!inTop && raw >= topSize) {
                return event.getCurrentItem();
            }
            return null;
        }
        if (!inTop) {
            return null;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return null;
        }
        if (click == ClickType.NUMBER_KEY) {
            int button = event.getHotbarButton();
            if (button >= 0 && button <= 8) {
                return player.getInventory().getItem(button);
            }
            if (button == 40) {
                return player.getInventory().getItemInOffHand();
            }
            return null;
        }
        if (click == ClickType.SWAP_OFFHAND) {
            return player.getInventory().getItemInOffHand();
        }
        if (click == ClickType.LEFT || click == ClickType.RIGHT) {
            return event.getCursor();
        }
        return null;
    }

    private boolean involvesLocked(InventoryClickEvent event) {
        if (plugin.getShulkers().isLocked(event.getCurrentItem()) || plugin.getShulkers().isLocked(event.getCursor())) {
            return true;
        }
        ClickType click = event.getClick();
        if (click != ClickType.NUMBER_KEY && click != ClickType.SWAP_OFFHAND) {
            return false;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return false;
        }
        int button = event.getHotbarButton();
        if (button >= 0 && button <= 8) {
            return plugin.getShulkers().isLocked(player.getInventory().getItem(button));
        }
        if (button == 40 || click == ClickType.SWAP_OFFHAND) {
            return plugin.getShulkers().isLocked(player.getInventory().getItemInOffHand());
        }
        return false;
    }

    private static void denyUse(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);
    }
}
