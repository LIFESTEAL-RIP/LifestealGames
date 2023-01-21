package com.etsuni.games.menus;


import com.etsuni.games.Games;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    private final Games plugin;

    public MenuListener(Games plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == null) {
            return;
        }
        InventoryHolder holder = event.getView().getTopInventory().getHolder();

        if(holder instanceof Menu) {
            if(event.getCurrentItem() == null) {return;}

            Menu menu = (Menu) holder;

            menu.handleMenu(event);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvMove(InventoryMoveItemEvent event) {
        InventoryHolder holder = event.getDestination().getHolder();

        if(holder instanceof Menu) {
            event.setCancelled(true);
        }
    }

}
