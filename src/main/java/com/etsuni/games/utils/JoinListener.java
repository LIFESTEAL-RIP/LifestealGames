package com.etsuni.games.utils;

import com.etsuni.games.Games;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final Games plugin;

    public JoinListener(Games plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        DBUtils dbUtils = new DBUtils(plugin);
        dbUtils.addPlayerToDB(player);
    }
}
