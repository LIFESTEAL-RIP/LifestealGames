package com.etsuni.games.games;

import com.etsuni.games.Games;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Events implements Listener {
    private final Games plugin;

    public Events(Games plugin) {
        this.plugin = plugin;
    }

    //This is to get an inputted number from the player to start their wager. Needs to check if there is any letters in the input
    // and see if it is above the min wager
    @EventHandler(priority = EventPriority.HIGHEST)
    public void getWagerFromChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        if(!ChatWagers.getInstance().getWaitingList().containsKey(player)){
            return;
        }
        String message = event.getMessage();
        long wager = 0;

        try {
            wager = Long.parseLong(message);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCoinflipConfig().getString("settings.messages.not_a_number")));
            ChatWagers.getInstance().getWaitingList().remove(player);
            event.setCancelled(true);
            return;
        }


        if(ChatWagers.getInstance().getWaitingList().containsKey(player)) {
            GameType gameType = ChatWagers.getInstance().getWaitingList().get(player);
            if (plugin.getEcon().getBalance(player) >= wager) {

                if (gameType.equals(GameType.COINFLIP)) {

                    if(wager < plugin.getCoinflipConfig().getInt("settings.min_wager")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.getCoinflipConfig().getString("settings.messages.under_min_wager")));
                        ChatWagers.getInstance().getWaitingList().remove(player);
                        event.setCancelled(true);
                        return;
                    }

                    Coinflip coinflip = new Coinflip(player, wager, plugin);
                    coinflip.addToGamesList();
                    ChatWagers.getInstance().getWaitingList().remove(player);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCoinflipConfig().getString("settings.messages.wager_inputted")
                            .replace("%wager%", String.valueOf(wager))));

                    plugin.getEcon().withdrawPlayer(player, wager);
                }
                //TODO ADD RPS AND CRASH HANDLERS
            } else {
                if(gameType.equals(GameType.COINFLIP)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCoinflipConfig().getString("settings.messages.not_enough_money")));
                    ChatWagers.getInstance().getWaitingList().remove(player);
                    event.setCancelled(true);
                    return;
                }

            }
        }
        event.setCancelled(true);
    }
}
