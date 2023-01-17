package com.etsuni.games.menus.coinflip;

import com.etsuni.games.Games;
import com.etsuni.games.games.ChatWagers;
import com.etsuni.games.games.Coinflip;
import com.etsuni.games.games.CurrentGames;
import com.etsuni.games.games.GameType;
import com.etsuni.games.menus.PaginatedMenu;
import com.etsuni.games.menus.PlayerMenuUtility;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CoinflipMainMenu extends PaginatedMenu {
    private final Games plugin;

    public CoinflipMainMenu(PlayerMenuUtility playerMenuUtility, Games plugin) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getCoinflipConfig().getString("main_menu.title")));
    }

    @Override
    public int getSlots() {
        return plugin.getCoinflipConfig().getInt("main_menu.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Configuration config = plugin.getCoinflipConfig();
        int slot = event.getSlot();
        ConfigurationSection items = config.getConfigurationSection("main_menu.items");

        for(String item : items.getKeys(false)) {
            if(slot == items.getInt(item + ".slot")) {
                if(item.equalsIgnoreCase("create_game")) {
                    ChatWagers.getInstance().getWaitingList().put(player, GameType.COINFLIP);
                    player.closeInventory();
                    player.sendTitle(
                            ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.wager_start.title")),
                            ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.wager_start.message")),
                            config.getInt("settings.messages.wager_start.fade_in"),
                            config.getInt("settings.messages.wager_start.stay"),
                            config.getInt("settings.messages.wager_start.fade_out"));
                }
                else if(item.equalsIgnoreCase("previous_page")) {
                    if(page > 0) {
                        page = page - 1;
                    }
                }
                else if(item.equalsIgnoreCase("next_page")) {
                    if(!((index + 1) >= CurrentGames.getInstance().getCoinflipGames().size())) {
                        page = page + 1;
                        open();
                    }
                }
            } else if(inventory.getItem(slot) != null && inventory.getItem(slot).getType().equals(Material.PLAYER_HEAD)){
                Coinflip coinflip = CurrentGames.getInstance().getCoinflipGames().get(index - 1);
                if(coinflip.getPlayer1().equals(player)) {
                    return;
                }
                if(plugin.getEcon().getBalance(player) >= coinflip.getWager()) {
                    coinflip.setPlayer2(player);
                    coinflip.start();
                } else {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.not_enough_money")));
                }
                return;
            }
        }
    }

    @Override
    public void setMenuItems() {
        //TODO ADD STAT HANDLING
        //TODO ADD LEADERBOARD HANDLING
        Configuration config = plugin.getCoinflipConfig();

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
                itemStack.addEnchantment(Enchantment.ARROW_INFINITE, 1);
            }

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

            itemStack.setItemMeta(meta);
            inventory.setItem(items.getInt(item + ".slot"), itemStack);
        }
        setMaxItemsPerPage(getSlots() - 9);

        List<Coinflip> coinflips = CurrentGames.getInstance().getCoinflipGames();
        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;
            if(index >= coinflips.size()) break;
            if(coinflips.get(index) != null) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                List<String> lore = new ArrayList<>();
                for(String str : config.getStringList("game_menu.player_heads_lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', str.replace("%wager%", coinflips.get(index).getWager().toString())));
                }
                Objects.requireNonNull(meta).setOwningPlayer(coinflips.get(i).getPlayer1());
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',Objects.requireNonNull(plugin.getCoinflipConfig().getString("main_menu.player_heads_name")
                        .replace("%player%", ChatColor.stripColor(coinflips.get(i).getPlayer1().getDisplayName())))));
                meta.setLore(lore);
                head.setItemMeta(meta);
                inventory.setItem(index, head);
            }
        }
    }
}
