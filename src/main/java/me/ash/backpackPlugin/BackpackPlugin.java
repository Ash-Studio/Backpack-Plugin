package me.ash.backpackPlugin;

import me.ash.backpackPlugin.commands.BackpackCommand;
import me.ash.backpackPlugin.data.BackpackManager;
import me.ash.backpackPlugin.gui.BackpackGui;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BackpackPlugin extends JavaPlugin {

    private BackpackManager backpackManager;
    private BackpackGui backpackGui;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            boolean created = getDataFolder().mkdirs();
            if (!created) {
                getLogger().warning("Failed to create plugin data folder!");
            }
        }

        backpackManager = new BackpackManager(this);
        backpackGui = new BackpackGui(backpackManager);
        getServer().getPluginManager().registerEvents(backpackGui, this);

        PluginCommand backpackCommand = getCommand("backpack");
        if (backpackCommand == null) {
            getLogger().severe("Failed to register 'backpack' command! Check your plugin.yml");
        } else {
            BackpackCommand executor = new BackpackCommand(this);
            backpackCommand.setExecutor(executor);
            backpackCommand.setTabCompleter(executor);
        }

        getLogger().info("BackpackPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (backpackManager != null) {
            // Save all backpacks when server shuts down
            for (Player player : getServer().getOnlinePlayers()) {
                backpackManager.saveBackpack(player);
            }
        }

        getLogger().info("BackpackPlugin has been disabled!");
    }

    public BackpackGui getBackpackGui() {
        return backpackGui;
    }
    
    public BackpackManager getBackpackManager() {
        return backpackManager;
    }
}