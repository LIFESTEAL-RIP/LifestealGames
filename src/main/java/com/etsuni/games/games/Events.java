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
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCoinflipConfig().getString("not_a_number")));
        }

        if(ChatWagers.getInstance().getWaitingList().containsKey(player)) {
            GameType gameType = ChatWagers.getInstance().getWaitingList().get(player);

            if(gameType.equals(GameType.COINFLIP)) {
                Coinflip coinflip = new Coinflip(player, wager, plugin);
                CurrentGames.getInstance().getCoinflipGames().add(coinflip);
                ChatWagers.getInstance().getWaitingList().remove(player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCoinflipConfig().getString("wager_inputted")
                        .replace("%wager%", String.valueOf(wager))));
            }
            //TODO ADD RPS AND CRASH HANDLERS
        }
    }
}
