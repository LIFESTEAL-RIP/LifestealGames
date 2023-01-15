package com.etsuni.games.games;

import com.etsuni.games.Games;
import org.bukkit.entity.Player;

public class Coinflip extends TwoPlayerGame{

    public Coinflip(Player player1, Long wager, Games plugin) {
        super(player1, wager, plugin);
    }


}
