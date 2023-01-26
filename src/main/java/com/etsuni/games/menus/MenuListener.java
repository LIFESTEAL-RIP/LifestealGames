package com.etsuni.games.menus;


import com.etsuni.games.Games;
import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    private final Games plugin;

    public MenuListener(Games plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent event) {

        if(event.getClickedInventory() == null) {
            return;
        }
        InventoryHolder holder = event.getView().getTopInventory().getHolder();

        if(holder instanceof Menu) {
            if(event.getClick() == ClickType.SWAP_OFFHAND) {
                ItemStack currentOffHand = event.getWhoClicked().getInventory().getItemInOffHand();
                event.getWhoClicked().getInventory().setItemInOffHand(currentOffHand);
                event.setCancelled(true);
                return;
            }
            if(event.getAction() == InventoryAction.HOTBAR_SWAP) {
                event.setCancelled(true);
                return;
            }
            if(event.getCurrentItem() == null) {return;}

            Menu menu = (Menu) holder;

            menu.handleMenu(event);
            event.setCancelled(true);
        }
    }

}
