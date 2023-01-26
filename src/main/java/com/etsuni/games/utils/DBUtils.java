package com.etsuni.games.utils;

import com.etsuni.games.Games;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DBUtils {

    private final Games plugin;

    public DBUtils(Games instance) {
        plugin = instance;
    }

    public void addPlayerToDB(Player player) {
        if(getPlayerInDB(player).isPresent()) {
            return;
        }

        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO players(uuid) VALUES(?);"
        )) {
            statement.setString(1, player.getUniqueId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public OptionalLong getPlayerInDB(Player player) {
        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM players WHERE uuid = ?;"
        )) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return  OptionalLong.of(resultSet.getLong("id"));
            }
            return OptionalLong.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return OptionalLong.empty();
        }
    }

    public void addWinAndProfitToPlayer(Player player, String game, Long profit) {
        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "UPDATE players SET " +game+"_wins = "+game+"_wins + 1,"
                        + game+ "_profit = " +game+"_profit + ? WHERE uuid = ?;"
        )) {
            statement.setLong(1, profit);
            statement.setString(2, player.getUniqueId().toString());
            statement.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLossToPlayer(Player player, String game) {
        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "UPDATE players SET " +game+"_losses = "+game+"_losses + 1 WHERE uuid = ?;"
        )) {
            statement.setString(1, player.getUniqueId().toString());
            statement.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public OptionalInt getPlayersWins(Player player, String game) {
        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "SELECT " + game + "_wins FROM players WHERE uuid = ?;"
        )) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return OptionalInt.of(resultSet.getInt(game + "_wins"));
            }
            return OptionalInt.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return OptionalInt.empty();
        }
    }

    public OptionalInt getPlayersLosses(Player player, String game) {
        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "SELECT " + game + "_losses FROM players WHERE uuid = ?;"
        )) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return OptionalInt.of(resultSet.getInt(game + "_losses"));
            }
            return OptionalInt.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return OptionalInt.empty();
        }
    }
    public OptionalLong getPlayersProfit(OfflinePlayer player, String game) {
        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "SELECT " + game + "_profit FROM players WHERE uuid = ?;"
        )) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return OptionalLong.of(resultSet.getInt(game + "_profit"));
            }
            return OptionalLong.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return OptionalLong.empty();
        }
    }

    public Optional<List<String>> getLeaderboard(String game) {
        List<String> leaderboard = new ArrayList<>();

        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "SELECT uuid FROM players ORDER BY " + game + "_profit DESC LIMIT 5"
        )) {
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                leaderboard.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
            return Optional.of(leaderboard);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
