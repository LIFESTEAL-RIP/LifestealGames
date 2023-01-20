package com.etsuni.games.menus;

import com.etsuni.games.Games;
import com.etsuni.games.menus.coinflip.CoinflipMainMenu;
import com.etsuni.games.menus.crash.CrashMainMenu;
import com.etsuni.games.menus.rps.RPSMainMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GamesMenu extends Menu {
    private final Games plugin;

    public GamesMenu(PlayerMenuUtility playerMenuUtility, Games plugin) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(plugin.getMainMenuConfig().getString("games_menu.title")));
    }

    @Override
    public int getSlots() {
        return plugin.getMainMenuConfig().getInt("games_menu.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Configuration config = plugin.getMainMenuConfig();
        ConfigurationSection items = config.getConfigurationSection("games_menu.items");
        int clickedSlot = event.getSlot();
        for(String item : items.getKeys(false)) {
                if(clickedSlot == config.getInt("games_menu.items.coinflip.slot")) {
                    CoinflipMainMenu coinflipMainMenu = new CoinflipMainMenu(plugin.getPlayerMenuUtility(player), plugin);
                    coinflipMainMenu.open();
                    return;
                }
                else if(clickedSlot == config.getInt("games_menu.items.rps.slot")) {
                    player.closeInventory();
                    RPSMainMenu rpsMainMenu = new RPSMainMenu(plugin.getPlayerMenuUtility(player), plugin);
                    rpsMainMenu.open();
                    return;
                }
                else if(clickedSlot == config.getInt("games_menu.items.crash.slot")) {
                    player.closeInventory();
                    CrashMainMenu crashMainMenu = new CrashMainMenu(plugin.getPlayerMenuUtility(player), plugin);
                    crashMainMenu.openHopper();
                   return;
                }
        }
    }

    @Override
    public void setMenuItems() {
        Configuration config = plugin.getMainMenuConfig();

        ConfigurationSection items = config.getConfigurationSection("games_menu.items");

        for(String item : items.getKeys(false)) {
            ItemStack itemStack = new ItemStack(Material.valueOf(items.getString(item + ".material")));
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', items.getString(item + ".name")));
            List<String> lore = new ArrayList<>();
            for(String s : items.getStringList(item + ".lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&',s));
            }
            meta.setLore(lore);
            if(items.getBoolean(item + ".enchanted")) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            }

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

            itemStack.setItemMeta(meta);
            inventory.setItem(items.getInt(item + ".slot"), itemStack);
        }
    }
}
