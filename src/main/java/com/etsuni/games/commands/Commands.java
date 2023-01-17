package com.etsuni.games.commands;

import com.etsuni.games.Games;
import com.etsuni.games.menus.GamesMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private final Games plugin;

    public Commands(Games plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(command.getName().equalsIgnoreCase("games")) {
                GamesMenu menu = new GamesMenu(plugin.getPlayerMenuUtility(((Player) sender).getPlayer()), plugin);
                menu.open();
            }
        }
        return false;
    }
}
