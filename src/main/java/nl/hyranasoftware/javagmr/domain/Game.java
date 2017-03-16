/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

import java.util.List;

/**
 *
 * @author danny_000
 */
public class Game {
    
    int gameid;
    String name;
    List<Player> players;
    CurrentTurn currentTurn;

    public int getGameid() {
        return gameid;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public CurrentTurn getCurrentTurn() {
        return currentTurn;
    }
    
    
    
}
