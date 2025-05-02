package me.ash.backpackPlugin.gui;

import me.ash.backpackPlugin.data.BackpackManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class BackpackGui implements Listener {

    private final BackpackManager backpackManager;
    private static final List<Material> BANNED_ITEMS = Arrays.asList(
        Material.ENDER_CHEST, 
        Material.SHULKER_BOX, 
        Material.BLACK_SHULKER_BOX,
        Material.BLUE_SHULKER_BOX,
        Material.BROWN_SHULKER_BOX,
        Material.CYAN_SHULKER_BOX,
        Material.GRAY_SHULKER_BOX,
        Material.GREEN_SHULKER_BOX,
        Material.LIGHT_BLUE_SHULKER_BOX,
        Material.LIGHT_GRAY_SHULKER_BOX,
        Material.LIME_SHULKER_BOX,
        Material.MAGENTA_SHULKER_BOX,
        Material.ORANGE_SHULKER_BOX,
        Material.PINK_SHULKER_BOX,
        Material.PURPLE_SHULKER_BOX,
        Material.RED_SHULKER_BOX,
        Material.WHITE_SHULKER_BOX,
        Material.YELLOW_SHULKER_BOX
    );

    public BackpackGui(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    public void openBackpack(Player player) {
        backpackManager.openBackpack(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Component title = view.title();

        if (title != null && title.equals(BackpackManager.BACKPACK_TITLE)) {
            // Check if the clicked item is a banned item
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && BANNED_ITEMS.contains(clickedItem.getType())) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("§cYou cannot store this item in your backpack!");
                return;
            }
            
            // Also block cursor items if they're banned
            ItemStack cursorItem = event.getCursor();
            if (cursorItem != null && !cursorItem.getType().isAir() && BANNED_ITEMS.contains(cursorItem.getType())) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("§cYou cannot store this item in your backpack!");
                return;
            }
            
            // Allow normal clicks for non-banned items
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryView view = event.getView();
        Component title = view.title();

        if (title != null && title.equals(BackpackManager.BACKPACK_TITLE)) {
            // Check if any of the dragged items are banned
            ItemStack draggedItem = event.getOldCursor();
            if (draggedItem != null && BANNED_ITEMS.contains(draggedItem.getType())) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("§cYou cannot store this item in your backpack!");
                return;
            }
            
            // Allow normal drags for non-banned items
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryView view = event.getView();
        Component title = view.title();

        if (title != null && title.equals(BackpackManager.BACKPACK_TITLE)) {
            Player player = (Player) event.getPlayer();
            player.sendMessage("§aSaving your backpack...");
            backpackManager.updateBackpackContents(player, event.getInventory());
        }
    }
    
    /**
     * Creates a decorated item for giving to players
     */
    public static ItemStack createBackpackItem() {
        ItemStack backpack = new ItemStack(Material.BUNDLE);
        ItemMeta meta = backpack.getItemMeta();
        
        meta.displayName(Component.text("Personal Backpack")
            .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
            .decoration(net.kyori.adventure.text.format.TextDecoration.BOLD, true));
        
        meta.lore(Arrays.asList(
            Component.text("Right-click to open your backpack")
                .color(net.kyori.adventure.text.format.NamedTextColor.GRAY),
            Component.text("Stores your items safely!")
                .color(net.kyori.adventure.text.format.NamedTextColor.GRAY)
        ));
        
        backpack.setItemMeta(meta);
        return backpack;
    }
}