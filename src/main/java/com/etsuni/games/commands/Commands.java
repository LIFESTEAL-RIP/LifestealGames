package com.etsuni.games.commands;

import com.etsuni.games.Games;
import com.etsuni.games.games.Coinflip;
import com.etsuni.games.games.CurrentGames;
import com.etsuni.games.games.RPS;
import com.etsuni.games.menus.GamesMenu;
import com.etsuni.games.menus.coinflip.CoinflipMainMenu;
import com.etsuni.games.menus.crash.CrashMainMenu;
import com.etsuni.games.menus.rps.RPSMainMenu;
import org.bukkit.Bukkit;
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
                if(args.length == 0) {
                    GamesMenu menu = new GamesMenu(plugin.getPlayerMenuUtility(((Player) sender).getPlayer()), plugin);
                    menu.open();
                }
                else {
                    if(!sender.hasPermission("games.remove")){
                        return false;
                    }
                    if(args[0].equalsIgnoreCase("remove")) {

                        if(args.length > 1) {
                            Player targetPlayer = Bukkit.getPlayer(args[1]);

                            if(targetPlayer != null) {

                                if(args.length > 2) {

                                    if(args[2].equalsIgnoreCase("cf")) {
                                        CurrentGames.getInstance().getCoinflipGames().removeIf(coinflip -> coinflip.getPlayer1().equals(targetPlayer));
                                    }
                                    else if(args[2].equalsIgnoreCase("rps")) {
                                        CurrentGames.getInstance().getRpsGames().removeIf(rps -> rps.getPlayer1().equals(targetPlayer));
                                    }
                                }
                            }
                        }
                    }
                }

            }
            else if(command.getName().equalsIgnoreCase("cf")) {
                CoinflipMainMenu coinflipMainMenu = new CoinflipMainMenu(plugin.getPlayerMenuUtility(((Player) sender).getPlayer()), plugin);
                coinflipMainMenu.open();
            }
            else if(command.getName().equalsIgnoreCase("rps")) {
                RPSMainMenu rpsMainMenu = new RPSMainMenu(plugin.getPlayerMenuUtility(((Player) sender).getPlayer()), plugin);
                rpsMainMenu.open();
            }
            else if(command.getName().equalsIgnoreCase("crash")) {
                CrashMainMenu crashMainMenu = new CrashMainMenu(plugin.getPlayerMenuUtility(((Player) sender).getPlayer()), plugin);
                crashMainMenu.openHopper();
            }
        }
        return false;
    }
}
