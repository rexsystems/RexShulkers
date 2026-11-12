package me.rexsystems.rexShulkers.command;

import me.rexsystems.rexShulkers.RexShulkers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ShulkersCommand implements CommandExecutor, TabCompleter {

    private final RexShulkers plugin;

    public ShulkersCommand(RexShulkers plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            plugin.getShulkers().sendHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> reload(sender);
            case "open" -> open(sender);
            case "give" -> give(sender, args);
            case "clear" -> clear(sender);
            default -> plugin.getShulkers().sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(List.of("help", "open", "give", "clear", "reload"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return filter(names, args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return filter(List.of("1", "8", "16", "32", "64"), args[2]);
        }
        return List.of();
    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission("rexshulkers.reload")) {
            plugin.getShulkers().sendConfigured(sender, "no-permission");
            return;
        }
        plugin.reloadPlugin();
        plugin.getShulkers().sendConfigured(sender, "reload");
    }

    private void open(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            plugin.getShulkers().sendConfigured(sender, "players-only");
            return;
        }
        if (!player.hasPermission("rexshulkers.use")) {
            plugin.getShulkers().sendConfigured(sender, "no-permission");
            return;
        }
        plugin.getShulkers().openCommand(player);
    }

    private void clear(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            plugin.getShulkers().sendConfigured(sender, "players-only");
            return;
        }
        if (!player.hasPermission("rexshulkers.clear")) {
            plugin.getShulkers().sendConfigured(sender, "no-permission");
            return;
        }
        plugin.getShulkers().clear(player);
    }

    private void give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rexshulkers.give")) {
            plugin.getShulkers().sendConfigured(sender, "no-permission");
            return;
        }
        if (args.length < 2) {
            plugin.getShulkers().sendConfigured(sender, "give-usage");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            plugin.getShulkers().sendConfigured(sender, "unknown-player");
            return;
        }
        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                plugin.getShulkers().sendConfigured(sender, "invalid-amount");
                return;
            }
        }
        if (amount < 1 || amount > 64) {
            plugin.getShulkers().sendConfigured(sender, "invalid-amount");
            return;
        }
        plugin.getShulkers().give(sender, target, amount);
    }

    private static List<String> filter(List<String> options, String token) {
        String prefix = token == null ? "" : token.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                matches.add(option);
            }
        }
        return matches;
    }
}
