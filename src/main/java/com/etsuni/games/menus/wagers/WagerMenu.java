package com.etsuni.games.menus.wagers;

import com.etsuni.games.Games;
import com.etsuni.games.games.GameType;
import com.etsuni.games.games.Wager;
import com.etsuni.games.menus.Menu;
import com.etsuni.games.menus.PlayerMenuUtility;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WagerMenu extends Menu {
    private final Games plugin;
    private final Wager wager;

    public WagerMenu(PlayerMenuUtility playerMenuUtility, Games plugin, Wager wager) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
        this.wager = wager;
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getWagersConfig().getString("menu_name")));
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Configuration config = plugin.getWagersConfig();
        int slot = event.getSlot();
        long minWager = 0L;

        if(wager.getGameType().equals(GameType.COINFLIP)) {
            minWager = minWager + plugin.getCoinflipConfig().getInt("settings.min_wager");
        }
        else if(wager.getGameType().equals(GameType.RPS)) {
            minWager = minWager + plugin.getRpsConfig().getInt("settings.min_wager");
        }
        else if(wager.getGameType().equals(GameType.CRASH)) {
            minWager = minWager + plugin.getCrashConfig().getInt("settings.min_wager");
        }

        switch (slot) {
            case 10:
                if(wager.getAmount() - 10000 >= minWager){
                    wager.setAmount(wager.getAmount() - 10000);
                    open();
                }

                break;
            case 11:
                if(wager.getAmount() - 1000 >= minWager){
                    wager.setAmount(wager.getAmount() - 1000);
                    open();
                }
                break;
            case 12:
                if(wager.getAmount() - 100 >= minWager){
                    wager.setAmount(wager.getAmount() - 100);
                    open();
                }
                break;
            case 13:
                if(!wager.sendWager()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("not_enough_money")));
                }
                break;
            case 14:
                wager.setAmount(wager.getAmount() + 100);
                open();
                break;
            case 15:
                wager.setAmount(wager.getAmount() + 1000);
                open();
                break;
            case 16:
                wager.setAmount(wager.getAmount() + 10000);
                open();
                break;
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        Configuration config = plugin.getWagersConfig();

        ItemStack border = new ItemStack(Material.valueOf(config.getString("border_item")));
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);

        ItemStack decrement = new ItemStack(Material.valueOf(config.getString("decrement_item.type")));
        ItemMeta decrementMeta = decrement.getItemMeta();
        List<String> decrementLore = new ArrayList<>();

        ItemStack increment = new ItemStack(Material.valueOf(config.getString("increment_item.type")));
        ItemMeta incrementMeta = increment.getItemMeta();
        List<String> incrementLore = new ArrayList<>();

        for(int i = 0; i < getSlots(); i++) {
            inventory.setItem(i, border);
        }
        int END_INDEX = 17;
        for(int i = 10; i < END_INDEX; i++) {
            switch (i) {
                case 10:
                    decrementMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            Objects.requireNonNull(config.getString("decrement_item.name_format")).replace("%amount%", "10,000")));

                    for(String s : config.getStringList("decrement_item.lore")) {
                        decrementLore.add(ChatColor.translateAlternateColorCodes('&', s));
                    }
                    decrementMeta.setLore(decrementLore);
                    decrement.setItemMeta(decrementMeta);
                    inventory.setItem(i, decrement);
                    break;
                case 11:
                    decrementMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            Objects.requireNonNull(config.getString("decrement_item.name_format")).replace("%amount%", "1,000")));
                    decrementMeta.setLore(decrementLore);
                    decrement.setItemMeta(decrementMeta);
                    inventory.setItem(i, decrement);
                    break;
                case 12:
                    decrementMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            Objects.requireNonNull(config.getString("decrement_item.name_format")).replace("%amount%", "100")));
                    decrementMeta.setLore(decrementLore);
                    decrement.setItemMeta(decrementMeta);
                    inventory.setItem(i, decrement);
                    break;
                case 13:
                    ItemStack currentWager = new ItemStack(Material.valueOf(config.getString("current_wager_item.type")));
                    ItemMeta currentWagerMeta = currentWager.getItemMeta();
                    currentWagerMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            config.getString("current_wager_item.name_format").replace("%amount%", wager.getAmount().toString())));
                    List<String> wagerLore = new ArrayList<>();
                    for(String s : config.getStringList("current_wager_item.lore")) {
                        if(s.contains("%game%")) {
                            wagerLore.add(ChatColor.translateAlternateColorCodes('&', s.replace("%game%", wager.getGameType().toString())));
                        } else {
                            wagerLore.add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                    }
                    currentWagerMeta.setLore(wagerLore);
                    currentWager.setItemMeta(currentWagerMeta);
                    inventory.setItem(i, currentWager);
                    break;
                case 14:
                    incrementMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            Objects.requireNonNull(config.getString("increment_item.name_format")).replace("%amount%", "100")));
                    for(String s : config.getStringList("increment_item.lore")) {
                        incrementLore.add(ChatColor.translateAlternateColorCodes('&', s));

                    }
                    incrementMeta.setLore(incrementLore);
                    increment.setItemMeta(incrementMeta);
                    inventory.setItem(i, increment);
                    break;
                case 15:
                    incrementMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            Objects.requireNonNull(config.getString("increment_item.name_format")).replace("%amount%", "1,000")));
                    incrementMeta.setLore(incrementLore);
                    increment.setItemMeta(incrementMeta);
                    inventory.setItem(i, increment);
                    break;
                case 16:
                    incrementMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            Objects.requireNonNull(config.getString("increment_item.name_format")).replace("%amount%", "10,000")));
                    incrementMeta.setLore(incrementLore);
                    increment.setItemMeta(incrementMeta);
                    inventory.setItem(i, increment);
                    break;
                default:
                    break;
            }

        }
    }
}
