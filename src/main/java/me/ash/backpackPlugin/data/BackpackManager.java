package me.ash.backpackPlugin.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackManager {

    private final Map<UUID, ItemStack[]> backpacks = new HashMap<>();
    private final JavaPlugin plugin;
    private final File dataFolder;

    // Match the same title format used in BackpackGui
    public static final Component BACKPACK_TITLE = Component.text("✦ Your Backpack ✦")
            .color(TextColor.color(65, 105, 225)) // Royal blue color
            .decoration(TextDecoration.BOLD, true);

    public BackpackManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "backpacks");

        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs();
            if (!created) {
                plugin.getLogger().warning("Failed to create backpacks directory!");
            }
        }
        loadAllBackpacks();
    }

    private void loadAllBackpacks() {
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName();
            String uuidStr = fileName.substring(0, fileName.length() - 4); // Remove .yml
            try {
                UUID playerId = UUID.fromString(uuidStr);
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                ItemStack[] contents = new ItemStack[getBackpackSizeFromConfig(config)];
                
                if (config.contains("contents")) {
                    for (int i = 0; i < contents.length; i++) {
                        if (config.contains("contents." + i)) {
                            contents[i] = config.getItemStack("contents." + i);
                        }
                    }
                }
                
                backpacks.put(playerId, contents);
                plugin.getLogger().info("Loaded backpack for player: " + uuidStr);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load backpack for player: " + uuidStr);
                e.printStackTrace();
            }
        }
    }

    private int getBackpackSizeFromConfig(YamlConfiguration config) {
        int size = config.getInt("size", 27); // Default to medium (3 rows)
        // Must be multiple of 9 and between 9 and 54
        return Math.min(54, Math.max(9, size - (size % 9)));
    }

    public int getBackpackSize(Player player) {
        // Determine backpack size based on permissions
        if (player.hasPermission("backpack.size.large")) {
            return 54; // 6 rows
        } else if (player.hasPermission("backpack.size.medium")) {
            return 36; // 4 rows
        } else {
            return 27; // 3 rows (default)
        }
    }

    public void openBackpack(Player player) {
        UUID playerId = player.getUniqueId();

        // Load the backpack if it exists on disk but not in memory
        if (!backpacks.containsKey(playerId)) {
            loadBackpack(player);
        }

        // If still not loaded, create a new backpack
        if (!backpacks.containsKey(playerId)) {
            int size = getBackpackSize(player);
            ItemStack[] contents = new ItemStack[size];
            Arrays.fill(contents, null); // Ensure the array is filled with nulls
            backpacks.put(playerId, contents);
        }

        // Check if the inventory size needs to be updated based on permissions
        ItemStack[] existingItems = backpacks.get(playerId);
        int currentSize = existingItems.length;
        int newSize = getBackpackSize(player);

        // If player got promoted and deserves a bigger backpack
        if (newSize > currentSize) {
            ItemStack[] newContents = new ItemStack[newSize];
            System.arraycopy(existingItems, 0, newContents, 0, currentSize);
            backpacks.put(playerId, newContents);
            existingItems = newContents;
        }

        // Use the colorful title instead of plain "Backpack"
        Inventory inv = Bukkit.createInventory(null, existingItems.length, BACKPACK_TITLE);
        inv.setContents(existingItems);
        player.openInventory(inv);
    }

    public void loadBackpack(Player player) {
        UUID playerId = player.getUniqueId();
        File file = new File(dataFolder, playerId + ".yml");
        
        if (file.exists()) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                int size = getBackpackSizeFromConfig(config);
                ItemStack[] contents = new ItemStack[size];
                
                if (config.contains("contents")) {
                    for (int i = 0; i < contents.length; i++) {
                        if (config.contains("contents." + i)) {
                            contents[i] = config.getItemStack("contents." + i);
                        }
                    }
                }
                
                backpacks.put(playerId, contents);
                player.sendMessage("§aLoaded your backpack!");
            } catch (Exception e) {
                player.sendMessage("§cFailed to load your backpack!");
                plugin.getLogger().warning("Failed to load backpack for player: " + player.getName());
                e.printStackTrace();
            }
        }
    }

    public void saveBackpack(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (!backpacks.containsKey(playerId)) {
            return; // Nothing to save
        }
        
        File file = new File(dataFolder, playerId + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        ItemStack[] contents = backpacks.get(playerId);
        
        config.set("size", contents.length);
        
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null) {
                config.set("contents." + i, item);
            }
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save backpack for player: " + player.getName());
            e.printStackTrace();
        }
    }

    // This method should be called when a player's inventory is closed
    public void updateBackpackContents(Player player, Inventory inventory) {
        UUID playerId = player.getUniqueId();
        ItemStack[] contents = inventory.getContents();
        backpacks.put(playerId, contents);
        saveBackpack(player);
    }

    public void clearBackpack(Player player) {
        UUID playerId = player.getUniqueId();
        int size = getBackpackSize(player);
        ItemStack[] emptyContents = new ItemStack[size];
        backpacks.put(playerId, emptyContents);
        saveBackpack(player);
        player.sendMessage("§aYour backpack has been cleared!");
    }

    public boolean hasBackpack(Player player) {
        UUID playerId = player.getUniqueId();
        return backpacks.containsKey(playerId);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}