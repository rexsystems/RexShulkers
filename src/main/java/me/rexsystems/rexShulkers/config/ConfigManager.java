package me.rexsystems.rexShulkers.config;

import me.rexsystems.rexShulkers.RexShulkers;
import me.rexsystems.rexShulkers.lock.MaterialBlacklist;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final RexShulkers plugin;
    private FileConfiguration config;
    private MaterialBlacklist blacklist = new MaterialBlacklist(List.of());

    public ConfigManager(RexShulkers plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        new ConfigAutoUpdater(plugin).update();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        this.blacklist = new MaterialBlacklist(config.getStringList("blacklist"));
    }

    public void reload() {
        load();
    }

    public int cooldownSeconds() {
        return Math.max(0, config.getInt("settings.cooldown-seconds", 3));
    }

    public boolean obfuscate() {
        return config.getBoolean("settings.obfuscate", true);
    }

    public boolean worldAllowed(String worldName) {
        if (worldName == null) {
            return false;
        }
        List<String> enabled = config.getStringList("worlds.enabled");
        if (enabled != null && !enabled.isEmpty()) {
            return enabled.stream().anyMatch(world -> world.equalsIgnoreCase(worldName));
        }
        List<String> disabled = config.getStringList("worlds.disabled");
        if (disabled == null || disabled.isEmpty()) {
            return true;
        }
        return disabled.stream().noneMatch(world -> world.equalsIgnoreCase(worldName));
    }

    public MaterialBlacklist blacklist() {
        return blacklist;
    }

    public String itemName() {
        return config.getString("item.name", "");
    }

    public List<String> itemLore() {
        List<String> lore = config.getStringList("item.lore");
        return lore == null ? List.of() : lore;
    }

    public String guiTitle() {
        String title = config.getString("gui.title", "Shulker");
        return title == null || title.isBlank() ? "Shulker" : title;
    }

    public int rows() {
        int rows = config.getInt("gui.rows", 3);
        if (rows < 1) {
            return 1;
        }
        if (rows > 6) {
            return 6;
        }
        return rows;
    }

    public int slots() {
        return rows() * 9;
    }

    public boolean soundEnabled(String which) {
        return config.getBoolean("sounds." + which + ".enabled", true);
    }

    public String soundName(String which) {
        String name = config.getString("sounds." + which + ".sound", "");
        return name == null ? "" : name.trim();
    }

    public float soundVolume(String which) {
        return (float) config.getDouble("sounds." + which + ".volume", 1.0);
    }

    public float soundPitch(String which) {
        return (float) config.getDouble("sounds." + which + ".pitch", 1.0);
    }

    public String message(String path) {
        if (config.isList("messages." + path)) {
            return "";
        }
        return config.getString("messages." + path, "");
    }

    public String prefix() {
        String prefix = message("prefix");
        return prefix == null ? "" : prefix;
    }

    public String prefixed(String path) {
        String raw = message(path);
        if (raw == null) {
            return "";
        }
        return raw.replace("<prefix>", prefix()).replace("{prefix}", prefix());
    }

    public List<String> helpLines() {
        List<String> lines = config.getStringList("messages.help");
        if (lines == null) {
            return List.of();
        }
        List<String> copy = new ArrayList<>();
        for (String line : lines) {
            if (line != null && !line.isBlank()) {
                copy.add(line);
            }
        }
        return copy;
    }

    public String databaseName() {
        return "shulkers.db";
    }
}
