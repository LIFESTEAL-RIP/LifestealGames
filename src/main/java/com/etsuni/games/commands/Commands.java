package com.etsuni.games.commands;

import com.etsuni.games.Games;
import com.etsuni.games.games.Coinflip;
import com.etsuni.games.games.Crash;
import com.etsuni.games.games.CurrentGames;
import com.etsuni.games.games.RPS;
import com.etsuni.games.menus.GamesMenu;
import com.etsuni.games.menus.coinflip.CoinflipMainMenu;
import com.etsuni.games.menus.crash.CrashMainMenu;
import com.etsuni.games.menus.rps.RPSMainMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Objects;

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
                if(args.length == 0) {
                    CoinflipMainMenu coinflipMainMenu = new CoinflipMainMenu(plugin.getPlayerMenuUtility(((Player) sender).getPlayer()), plugin);
                    coinflipMainMenu.open();
                }

                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("create")) {
                        Configuration config = plugin.getCoinflipConfig();
                        if(playerAtMaxGames(((Player) sender).getPlayer(), config)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.too_many_games")));
                            return false;
                        }

                        if(args.length > 1) {
                            long wager = 0L;
                            try {
                                wager = Long.parseLong(args[1]);
                                if (plugin.getEcon().getBalance(((Player) sender).getPlayer()) >= wager) {

                                    if (wager < plugin.getCoinflipConfig().getInt("settings.min_wager")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.getCoinflipConfig().getString("settings.messages.under_min_wager")));
                                        return false;
                                    }

                                    Coinflip coinflip = new Coinflip(((Player) sender).getPlayer(), wager, plugin);
                                    coinflip.addToGamesList();
                                    plugin.getEcon().withdrawPlayer(((Player) sender).getPlayer(), wager);
                                    ((Player) sender).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCoinflipConfig().getString("settings.messages.wager_inputted")
                                            .replace("%wager%", String.valueOf(wager))));

                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.getCoinflipConfig().getString("settings.messages.not_enough_money")));
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        Objects.requireNonNull(plugin.getCoinflipConfig().getString("settings.messages.not_a_number"))));
                                return false;
                            }
                        }else {
                            sender.sendMessage(ChatColor.RED + "Usage: /cf create <wager>");
                        }
                    }
                }

            }
            else if(command.getName().equalsIgnoreCase("rps")) {
                if(args.length == 0) {
                    RPSMainMenu rpsMainMenu = new RPSMainMenu(plugin.getPlayerMenuUtility(((Player) sender).getPlayer()), plugin);
                    rpsMainMenu.open();
                }

                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("create")) {
                        Configuration config = plugin.getRpsConfig();
                        if(playerAtMaxGames(((Player) sender).getPlayer(), config)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("settings.messages.too_many_games")));
                            return false;
                        }

                        if(args.length > 2) {
                            if(args[2].equalsIgnoreCase("rock") || args[2].equalsIgnoreCase("paper") || args[2].equalsIgnoreCase("scissors")) {
                                long wager = 0L;
                                try {
                                    wager = Long.parseLong(args[1]);
                                    if (plugin.getEcon().getBalance(((Player) sender).getPlayer()) >= wager) {

                                        if (wager < plugin.getRpsConfig().getInt("settings.min_wager")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                    plugin.getRpsConfig().getString("settings.messages.under_min_wager")));
                                            return false;
                                        }

                                        RPS rps = new RPS(((Player) sender).getPlayer(), wager, plugin);
                                        rps.setPlayer1Choice(RPS.Choice.valueOf(args[2].toUpperCase()));
                                        rps.addToList();
                                        plugin.getEcon().withdrawPlayer(((Player) sender).getPlayer(), wager);
                                        ((Player) sender).getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getRpsConfig().getString("settings.messages.wager_inputted")
                                                .replace("%wager%", String.valueOf(wager))));

                                    } else {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.getRpsConfig().getString("settings.messages.not_enough_money")));
                                    }
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            Objects.requireNonNull(plugin.getRpsConfig().getString("settings.messages.not_a_number"))));
                                    return false;
                                }
                            }
                            else {
                                sender.sendMessage(ChatColor.RED + "Usage: /rps create <wager> <rock|paper|scissors>");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /rps create <wager> <rock|paper|scissors>");
                        }
                    }
                }
            }
            else if(command.getName().equalsIgnoreCase("crash")) {
                if(args.length == 0) {
                    CrashMainMenu crashMainMenu = new CrashMainMenu(plugin.getPlayerMenuUtility(((Player) sender).getPlayer()), plugin);
                    crashMainMenu.openHopper();
                }

                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("create")) {

                        if(args.length > 1) {
                            long wager = 0L;
                            try {
                                wager = Long.parseLong(args[1]);
                                if (plugin.getEcon().getBalance(((Player) sender).getPlayer()) >= wager) {

                                    if (wager < plugin.getCrashConfig().getInt("settings.min_wager")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.getCrashConfig().getString("settings.messages.under_min_wager")));
                                        return false;
                                    }

                                    Crash crash = new Crash(((Player) sender).getPlayer(), wager, plugin);
                                    crash.start();
                                    plugin.getEcon().withdrawPlayer(((Player) sender).getPlayer(), wager);
                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.getCrashConfig().getString("settings.messages.not_enough_money")));
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        Objects.requireNonNull(plugin.getCrashConfig().getString("settings.messages.not_a_number"))));
                                return false;
                            }
                        }else {
                            sender.sendMessage(ChatColor.RED + "Usage: /crash create <wager>");
                        }
                    }
                }
            }
        }
        return false;
    }

    private Boolean playerAtMaxGames(Player player, Configuration config) {
        if(config.equals(plugin.getCoinflipConfig())) {
            int maxGames = config.getInt("settings.max_player_games");
            int count = 0;
            for(Coinflip coinflip : CurrentGames.getInstance().getCoinflipGames()) {
                if(coinflip.getPlayer1().equals(player)) {
                    count++;
                }
            }
            return count >= maxGames;
        }
        else if(config.equals(plugin.getRpsConfig())) {
            int maxGames = config.getInt("settings.max_player_games");
            int count = 0;
            for(RPS rps : CurrentGames.getInstance().getRpsGames()) {
                if(rps.getPlayer1().equals(player)) {
                    count++;
                }
            }
            return count >= maxGames;
        }

        return false;
    }
}
