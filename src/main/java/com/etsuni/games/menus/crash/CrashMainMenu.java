package com.etsuni.games.menus.crash;

import com.etsuni.games.Games;
import com.etsuni.games.games.ChatWagers;
import com.etsuni.games.games.Coinflip;
import com.etsuni.games.games.CurrentGames;
import com.etsuni.games.games.GameType;
import com.etsuni.games.menus.Menu;
import com.etsuni.games.menus.PlayerMenuUtility;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CrashMainMenu extends Menu {

    private final Games plugin;

    public CrashMainMenu(PlayerMenuUtility playerMenuUtility, Games plugin) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;

    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getCrashConfig().getString("main_menu.title")));
    }

    @Override
    public int getSlots() {
        return plugin.getCrashConfig().getInt("main_menu.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Configuration config = plugin.getCrashConfig();
        int slot = event.getSlot();
        ConfigurationSection items = config.getConfigurationSection("main_menu.items");

        for(String item : items.getKeys(false)) {
            if(slot == items.getInt(item + ".slot")) {
                if(item.equalsIgnoreCase("create_game")) {
                    ChatWagers.getInstance().getWaitingList().put(player, GameType.CRASH);
                    player.closeInventory();
                    player.sendTitle(
                            ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.wager_start.title")),
                            ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.wager_start.message")),
                            config.getInt("settings.messages.wager_start.fade_in"),
                            config.getInt("settings.messages.wager_start.stay"),
                            config.getInt("settings.messages.wager_start.fade_out"));
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        //TODO ADD STAT HANDLING
        //TODO ADD LEADERBOARD HANDLING
        Configuration config = plugin.getCrashConfig();

        ConfigurationSection items = config.getConfigurationSection("main_menu.items");

        for(String item : items.getKeys(false)) {
            ItemStack itemStack = new ItemStack(Material.valueOf(items.getString(item + ".material")));
            ItemMeta meta;

            if(itemStack.getType().equals(Material.PLAYER_HEAD)) {
                SkullMeta headMeta = (SkullMeta) itemStack.getItemMeta();
                headMeta.setOwningPlayer(this.playerMenuUtility.getOwner());
                meta = headMeta;
            } else{
                meta = itemStack.getItemMeta();
            }

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', items.getString(item + ".name")));
            List<String> lore = new ArrayList<>();
            for(String s : items.getStringList(item + ".lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&',s));
            }
            meta.setLore(lore);
            if(items.getBoolean(item + ".enchanted")) {
                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            }

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

            itemStack.setItemMeta(meta);
            inventory.setItem(items.getInt(item + ".slot"), itemStack);
        }
    }
}
