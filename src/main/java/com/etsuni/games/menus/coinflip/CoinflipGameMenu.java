package com.etsuni.games.menus.coinflip;

import com.etsuni.games.Games;
import com.etsuni.games.games.Coinflip;
import com.etsuni.games.menus.Menu;
import com.etsuni.games.menus.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

public class CoinflipGameMenu extends Menu {

    private final Games plugin;
    private final Coinflip coinflip;
    private int taskId;
    private int flipCount = 0;
    private Player currentPlayer;

    public CoinflipGameMenu(PlayerMenuUtility playerMenuUtility, Games plugin, Coinflip coinflip) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
        this.coinflip = coinflip;
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getCoinflipConfig().getString("game_menu.title")));
    }

    @Override
    public int getSlots() {
        return plugin.getCoinflipConfig().getInt("game_menu.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        //DON'T NEED TO DO ANYTHING HERE AS THE EVENT IS CANCELLED ALREADY
    }

    @Override
    public void setMenuItems() {
        for(int i = 0; i < getInventory().getSize(); i++) {
            getInventory().setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        currentPlayer = coinflip.getPlayer1();



    }

    public void setPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(plugin.getCoinflipConfig().getString("game_menu.player_head_name").replace("%player%", player.getDisplayName()));
        head.setItemMeta(meta);
        inventory.setItem(13, head);
    }
}
