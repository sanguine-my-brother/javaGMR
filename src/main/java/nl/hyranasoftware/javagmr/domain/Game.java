/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author danny_000
 */
public class Game {
    
    @JsonProperty("GameId")
    int gameid;
    @JsonProperty("Name")
    String name;
    @JsonProperty("Players")
    List<Player> players;
    @JsonProperty("CurrentTurn")
    CurrentTurn currentTurn;
    @JsonProperty("Type")
    int type;

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
