package me.rexsystems.rexShulkers.item;

import me.rexsystems.rexShulkers.util.TextParser;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ShulkerItems {

    private final NamespacedKey key;

    public ShulkerItems(Plugin plugin) {
        this.key = new NamespacedKey(plugin, "id");
    }

    public NamespacedKey key() {
        return key;
    }

    public ItemStack create(UUID id, String name, List<String> lore) {
        ItemStack stack = new ItemStack(Material.SHULKER_BOX);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }
        writeId(meta, id);
        if (name != null && !name.isBlank()) {
            meta.displayName(TextParser.parse(name));
        }
        if (lore != null && !lore.isEmpty()) {
            List<Component> lines = new ArrayList<>();
            for (String line : lore) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                lines.add(TextParser.parse(line));
            }
            if (!lines.isEmpty()) {
                meta.lore(lines);
            }
        }
        if (meta instanceof BlockStateMeta blockMeta && blockMeta.getBlockState() instanceof ShulkerBox box) {
            box.getInventory().clear();
            blockMeta.setBlockState(box);
            writeId(blockMeta, id);
            stack.setItemMeta(blockMeta);
            return stack;
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public UUID read(ItemStack stack) {
        if (stack == null || stack.getType().isAir() || !stack.hasItemMeta()) {
            return null;
        }
        String raw = stack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return new ShulkerTag(ShulkerTag.parse(raw)).id();
    }

    public UUID clear(ItemStack stack) {
        UUID id = read(stack);
        if (id == null || stack == null) {
            return null;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return null;
        }
        meta.getPersistentDataContainer().remove(key);
        stack.setItemMeta(meta);
        return new ShulkerTag(id).clear();
    }

    public void clearBlockInventory(ItemStack stack) {
        UUID id = read(stack);
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }
        if (!(stack.getItemMeta() instanceof BlockStateMeta meta)) {
            return;
        }
        if (!(meta.getBlockState() instanceof ShulkerBox box)) {
            return;
        }
        box.getInventory().clear();
        meta.setBlockState(box);
        if (id != null) {
            writeId(meta, id);
        }
        stack.setItemMeta(meta);
    }

    private void writeId(ItemMeta meta, UUID id) {
        if (meta == null || id == null) {
            return;
        }
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id.toString());
    }
}
