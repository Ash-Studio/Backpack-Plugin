package me.ash.backpackPlugin.commands;

import me.ash.backpackPlugin.BackpackPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BackpackCommand implements CommandExecutor, TabCompleter {

    private final BackpackPlugin plugin;
    private final List<String> subCommands = Arrays.asList("open", "clear", "help");

    public BackpackCommand(BackpackPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("open")) {
            // Use the GUI to open the backpack
            plugin.getBackpackGui().openBackpack(player);
            return true;
        } else if (args[0].equalsIgnoreCase("clear")) {
            if (!player.hasPermission("backpack.command.clear")) {
                player.sendMessage("§cYou don't have permission to clear your backpack!");
                return true;
            }
            
            plugin.getBackpackManager().clearBackpack(player);
            return true;
        } else if (args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(player);
            return true;
        } else if (args[0].equalsIgnoreCase("admin") && args.length > 1 && player.hasPermission("backpack.admin")) {
            // Admin commands
            if (args[1].equalsIgnoreCase("open") && args.length > 2) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage("§cPlayer not found!");
                    return true;
                }
                
                player.sendMessage("§aOpening " + target.getName() + "'s backpack...");
                plugin.getBackpackGui().openBackpack(target);
                return true;
            } else if (args[1].equalsIgnoreCase("clear") && args.length > 2) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    player.sendMessage("§cPlayer not found!");
                    return true;
                }
                
                plugin.getBackpackManager().clearBackpack(target);
                player.sendMessage("§aCleared " + target.getName() + "'s backpack!");
                return true;
            }
        }
        
        // If we reach here, invalid command syntax
        sendHelpMessage(player);
        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== Backpack Plugin Help ===");
        player.sendMessage("§e/backpack §7- Open your backpack");
        player.sendMessage("§e/backpack open §7- Open your backpack");
        player.sendMessage("§e/backpack clear §7- Clear your backpack");
        player.sendMessage("§e/backpack help §7- Show this help message");
        
        if (player.hasPermission("backpack.admin")) {
            player.sendMessage("§6=== Admin Commands ===");
            player.sendMessage("§e/backpack admin open <player> §7- Open another player's backpack");
            player.sendMessage("§e/backpack admin clear <player> §7- Clear another player's backpack");
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }
        
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument (open, clear, help, admin)
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
            
            if (sender.hasPermission("backpack.admin") && "admin".startsWith(args[0].toLowerCase())) {
                completions.add("admin");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("backpack.admin")) {
            // Admin sub-commands
            if ("open".startsWith(args[1].toLowerCase())) completions.add("open");
            if ("clear".startsWith(args[1].toLowerCase())) completions.add("clear");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin") && 
                  (args[1].equalsIgnoreCase("open") || args[1].equalsIgnoreCase("clear")) && 
                   sender.hasPermission("backpack.admin")) {
            // Player name completion
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
}