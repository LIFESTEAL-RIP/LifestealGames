package com.etsuni.games.menus.rps;

import com.etsuni.games.Games;
import com.etsuni.games.games.*;
import com.etsuni.games.menus.PaginatedMenu;
import com.etsuni.games.menus.PlayerMenuUtility;
import com.etsuni.games.utils.DBUtils;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RPSMainMenu extends PaginatedMenu {
    private final Games plugin;

    public RPSMainMenu(PlayerMenuUtility playerMenuUtility, Games plugin) {
        super(playerMenuUtility, plugin);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getRpsConfig().getString("main_menu.title"));
    }

    @Override
    public int getSlots() {
        return plugin.getRpsConfig().getInt("main_menu.size");
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        Configuration config = plugin.getRpsConfig();
        int slot = event.getSlot();
        ConfigurationSection items = config.getConfigurationSection("main_menu.items");

        if(slot == getSlots() - 9){
            return;
        }

        for(String item : items.getKeys(false)) {
            if(slot == items.getInt(item + ".slot")) {
                if(item.equalsIgnoreCase("create_game")) {
                    ChatWagers.getInstance().getWaitingList().put(player, GameType.RPS);
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
                    if(!((index + 1) >= CurrentGames.getInstance().getRpsGames().size())) {
                        page = page + 1;
                        open();
                    }
                }
            } else if(inventory.getItem(slot) != null && inventory.getItem(slot).getType().equals(Material.PLAYER_HEAD)){
                RPS rps = CurrentGames.getInstance().getRpsGames().get(index - 1);
                if(rps.getPlayer1().equals(player)) {
                    return;
                }
                if(plugin.getEcon().getBalance(player) >= rps.getWager()) {
                    player.closeInventory();
                    rps.sendChoiceTitle(player);
                    ChatWagers.getInstance().getRpsChoicesBeingWaitedOn().put(player, CurrentGames.getInstance().getRpsGames().get(index - 1));
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
        Configuration config = plugin.getRpsConfig();

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
            if(item.equalsIgnoreCase("stats")) {
                meta.setLore(getStats());
            }
            else if(item.equalsIgnoreCase("leaderboard")) {
                meta.setLore(getLeaderboard());
            }
            else {
                List<String> lore = new ArrayList<>();
                for(String s : items.getStringList(item + ".lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&',s));
                }
                meta.setLore(lore);
            }
            if(items.getBoolean(item + ".enchanted")) {
                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
            }

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

            itemStack.setItemMeta(meta);
            inventory.setItem(items.getInt(item + ".slot"), itemStack);
        }
        setMaxItemsPerPage(getSlots() - 9);

        List<RPS> rpsGames = CurrentGames.getInstance().getRpsGames();
        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;
            if(index >= rpsGames.size()) break;
            if(rpsGames.get(index) != null) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                List<String> lore = new ArrayList<>();
                for(String str : config.getStringList("main_menu.player_heads_lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', str.replace("%wager%", rpsGames.get(index).getWager().toString())));
                }
                Objects.requireNonNull(meta).setOwningPlayer(rpsGames.get(i).getPlayer1());
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',Objects.requireNonNull(plugin.getRpsConfig().getString("main_menu.player_heads_name")
                        .replace("%player%", ChatColor.stripColor(rpsGames.get(i).getPlayer1().getDisplayName())))));
                meta.setLore(lore);
                head.setItemMeta(meta);
                inventory.setItem(index, head);
            }
        }
    }

    private List<String> getStats() {
        List<String> statsLore = new ArrayList<>();
        DBUtils dbUtils = new DBUtils(plugin);
        Configuration config = plugin.getRpsConfig();
        ConfigurationSection stats = config.getConfigurationSection("main_menu.items.stats");
        for(String s : stats.getStringList("lore")) {
            if(s.contains("%wins%")) {
                statsLore.add(ChatColor.translateAlternateColorCodes('&',
                        s.replace("%wins%", String.valueOf(dbUtils.getPlayersWins(playerMenuUtility.getOwner(), "rps").getAsInt()))));
            }
            else if(s.contains("%losses%")) {
                statsLore.add(ChatColor.translateAlternateColorCodes('&',
                        s.replace("%losses%", String.valueOf(dbUtils.getPlayersLosses(playerMenuUtility.getOwner(), "rps").getAsInt()))));
            }
            else if(s.contains("%profit%")) {
                statsLore.add(ChatColor.translateAlternateColorCodes('&',
                        s.replace("%profit%", String.valueOf(dbUtils.getPlayersProfit(playerMenuUtility.getOwner(), "rps").getAsLong()))));
            }
            else if(s.contains("%win_percentage%")) {
                statsLore.add(ChatColor.translateAlternateColorCodes('&',
                        s.replace("%win_percentage%", calcWinPercentage())));
            }
        }

        return statsLore;
    }

    private List<String> getLeaderboard() {
        DBUtils dbUtils = new DBUtils(plugin);
        List<String> dbLeaderboard = dbUtils.getLeaderboard("rps").get();
        List<String> leaderboard = new ArrayList<>();
        Configuration config = plugin.getRpsConfig();
        ConfigurationSection board = config.getConfigurationSection("main_menu.items.leaderboard");
        for(String player : dbLeaderboard) {
            if(player == null) {
                continue;
            }
            leaderboard.add(ChatColor.translateAlternateColorCodes('&',
                    board.getString("lore_format")
                            .replace("%number%", String.valueOf(dbLeaderboard.indexOf(player) + 1))
                            .replace("%player_name%", player)
                            .replace("%player_profit%", String.valueOf(dbUtils.getPlayersProfit(Bukkit.getOfflinePlayer(player), "rps").getAsLong()))
            ));
        }

        return leaderboard;
    }

    private String calcWinPercentage() {
        DBUtils dbUtils = new DBUtils(plugin);
        int wins = dbUtils.getPlayersWins(playerMenuUtility.getOwner(), "rps").getAsInt();
        int losses = dbUtils.getPlayersLosses(playerMenuUtility.getOwner(), "rps").getAsInt();
        DecimalFormat df = new DecimalFormat("0.00");
        if(losses < 1) {
            return df.format(100.00);
        }
        if(wins < 1) {
            return df.format(0.00);
        }

        double gamesPlayed = wins + losses;
        double calcPercent = (double) (wins / gamesPlayed);

        return df.format(calcPercent * 100);
    }
}
