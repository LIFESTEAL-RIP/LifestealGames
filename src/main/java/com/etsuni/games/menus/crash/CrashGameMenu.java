package com.etsuni.games.menus.crash;

import com.etsuni.games.Games;
import com.etsuni.games.games.Crash;
import com.etsuni.games.menus.Menu;
import com.etsuni.games.menus.PlayerMenuUtility;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CrashGameMenu extends Menu {

    private final Games plugin;
    public Crash crash;

    public CrashGameMenu(PlayerMenuUtility playerMenuUtility, Crash crash, Games plugin) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
        this.crash = crash;
    }

    @Override
    public String getMenuName() {
        return null;
    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

    }

    @Override
    public void setMenuItems() {

    }
}
