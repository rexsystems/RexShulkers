package me.rexsystems.rexShulkers.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.rexsystems.rexShulkers.RexShulkers;
import org.bukkit.entity.Player;

public final class RexShulkersPlaceholders extends PlaceholderExpansion {

    private final RexShulkers plugin;

    public RexShulkersPlaceholders(RexShulkers plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "rexshulkers";
    }

    @Override
    public String getAuthor() {
        return "RexSystems";
    }

    @Override
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) {
            return "";
        }
        if (params.equalsIgnoreCase("open")) {
            return plugin.getShulkers().hasOpen(player.getUniqueId()) ? "yes" : "no";
        }
        if (params.equalsIgnoreCase("cooldown")) {
            return Long.toString(plugin.getShulkers().cooldownRemainingSeconds(player.getUniqueId()));
        }
        return null;
    }
}
