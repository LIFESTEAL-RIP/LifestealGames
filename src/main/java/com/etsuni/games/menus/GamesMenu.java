package com.etsuni.games.menus;

import com.etsuni.games.Games;
import com.etsuni.games.menus.coinflip.CoinflipMainMenu;
import org.bukkit.Bukkit;
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
        //TODO MAKE IT SO WHEN PLAYER CLICKS ITEMS WILL CLOSE THIS INV AND OPEN OTHER RESPECTIVE INV
        Player player = (Player) event.getWhoClicked();

        Configuration config = plugin.getMainMenuConfig();
        ConfigurationSection items = config.getConfigurationSection("games_menu.items");

        for(String item : items.getKeys(false)) {
                if(item.equalsIgnoreCase("coinflip")) {
                    CoinflipMainMenu coinflipMainMenu = new CoinflipMainMenu(plugin.getPlayerMenuUtility(player), plugin);
                    coinflipMainMenu.open();
                    return;
                }
                else if(item.equalsIgnoreCase("rps")) {
                    player.closeInventory();
                    //TODO ADD RPS HANDLING
                }
                else if(item.equalsIgnoreCase("crash")) {
                    player.closeInventory();
                    //TODO ADD CRASH GAME HANDLING
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
