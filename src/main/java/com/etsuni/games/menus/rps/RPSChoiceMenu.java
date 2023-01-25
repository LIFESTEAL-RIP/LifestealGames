package com.etsuni.games.menus.rps;

import com.etsuni.games.Games;
import com.etsuni.games.games.CurrentGames;
import com.etsuni.games.games.RPS;
import com.etsuni.games.menus.Menu;
import com.etsuni.games.menus.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RPSChoiceMenu extends Menu {

    private final Games plugin;
    private final RPS rps;

    public RPSChoiceMenu(PlayerMenuUtility playerMenuUtility, RPS rps, Games plugin) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
        this.rps = rps;
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getRpsConfig().getString("selection_menu.title"));
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        switch(slot) {
            case 11:
                if(rps.getPlayer1Choice() == null) {
                    rps.setPlayer1Choice(RPS.Choice.ROCK);
                    CurrentGames.getInstance().getRpsGames().add(rps);
                    sendWagerMsg();
                } else {
                    if(!rps.start(RPS.Choice.ROCK, player)) {
                        player.sendMessage(ChatColor.RED + "Invalid game.");
                    }
                }
                player.closeInventory();
                break;
            case 13:
                if(rps.getPlayer1Choice() == null) {
                    rps.setPlayer1Choice(RPS.Choice.PAPER);
                    CurrentGames.getInstance().getRpsGames().add(rps);
                    sendWagerMsg();
                } else {
                    if(!rps.start(RPS.Choice.PAPER, player)) {
                        player.sendMessage(ChatColor.RED + "Invalid game.");
                    }
                }
                player.closeInventory();
                break;
            case 15:
                if(rps.getPlayer1Choice() == null) {
                    rps.setPlayer1Choice(RPS.Choice.SCISSORS);
                    CurrentGames.getInstance().getRpsGames().add(rps);
                    sendWagerMsg();
                } else {
                    if(!rps.start(RPS.Choice.SCISSORS, player)) {
                        player.sendMessage(ChatColor.RED + "Invalid game.");
                    }
                }
                player.closeInventory();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        Configuration config = plugin.getRpsConfig();

        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);

        ConfigurationSection items = config.getConfigurationSection("selection_menu.items");
        ItemStack item = new ItemStack(Material.valueOf(items.getString("border.type")));
        ItemMeta meta = item.getItemMeta();

        for(int i  = 0; i < getSlots(); i++) {
            switch(i) {
                case 11:
                    item = new ItemStack(Material.valueOf(items.getString("rock.type")));
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', items.getString("rock.name")));
                    item.setItemMeta(meta);
                    inventory.setItem(i, item);
                    break;
                case 13:
                    item = new ItemStack(Material.valueOf(items.getString("paper.type")));
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', items.getString("paper.name")));
                    item.setItemMeta(meta);
                    inventory.setItem(i, item);
                    break;
                case 15:
                    item = new ItemStack(Material.valueOf(items.getString("scissors.type")));
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', items.getString("scissors.name")));
                    item.setItemMeta(meta);
                    inventory.setItem(i, item);
                    break;
                default:
                    item = new ItemStack(Material.valueOf(items.getString("border.type")));
                    meta.setDisplayName(" ");
                    item.setItemMeta(meta);
                    inventory.setItem(i, item);
                    break;
            }
        }
    }

    private void sendWagerMsg() {
        playerMenuUtility.getOwner().sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getRpsConfig().getString("settings.messages.wager_inputted").replace("%wager%", rps.getWager().toString())));
    }
}
