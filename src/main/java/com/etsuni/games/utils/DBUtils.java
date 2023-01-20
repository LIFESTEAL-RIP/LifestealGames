package com.etsuni.games.utils;

import com.etsuni.games.Games;
import org.bukkit.Bukkit;
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
                "REPLACE players(" + game +"_wins, " + game + "_profit) VALUES(?,?);"
        )) {
            statement.setString(1, String.valueOf(getPlayersWins(player, game).getAsInt() + 1));
            statement.setString(2, String.valueOf(getPlayersProfit(player,game).getAsLong() + profit));
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

    public OptionalLong getPlayersProfit(Player player, String game) {
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

    public Optional<List<Player>> getLeaderboard(String game) {
        List<Player> leaderboard = new ArrayList<>();

        try(Connection conn = plugin.getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(
                "SELECT uuid FROM players ORDER BY " + game + "_profit DESC LIMIT 5"
        )) {
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                leaderboard.add(Bukkit.getPlayer(resultSet.getString("uuid")));
            }
            return Optional.of(leaderboard);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
