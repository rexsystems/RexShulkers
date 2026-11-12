package me.rexsystems.rexShulkers;

import me.rexsystems.rexShulkers.command.ShulkersCommand;
import me.rexsystems.rexShulkers.config.ConfigManager;
import me.rexsystems.rexShulkers.item.ShulkerItems;
import me.rexsystems.rexShulkers.listener.ShulkerListener;
import me.rexsystems.rexShulkers.papi.RexShulkersPlaceholders;
import me.rexsystems.rexShulkers.storage.ShulkerRepository;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class RexShulkers extends JavaPlugin {

    private ConfigManager configManager;
    private ShulkerService shulkers;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.configManager.load();

        ShulkerRepository repository = new ShulkerRepository(new File(getDataFolder(), configManager.databaseName()), getLogger());
        try {
            repository.open();
        } catch (Exception ex) {
            getLogger().severe("Could not open shulkers.db: " + ex.getMessage());
            repository.close();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.shulkers = new ShulkerService(this, new ShulkerItems(this), repository);
        getServer().getPluginManager().registerEvents(new ShulkerListener(this), this);

        ShulkersCommand command = new ShulkersCommand(this);
        PluginCommand pluginCommand = getCommand("rexshulkers");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RexShulkersPlaceholders(this).register();
            getLogger().info("PlaceholderAPI expansion registered.");
        }

        getLogger().info("RexShulkers enabled. Storage: " + configManager.databaseName());
    }

    @Override
    public void onDisable() {
        if (shulkers != null) {
            shulkers.shutdown();
            shulkers = null;
        }
        getLogger().info("RexShulkers disabled.");
    }

    public void reloadPlugin() {
        configManager.reload();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ShulkerService getShulkers() {
        return shulkers;
    }
}
