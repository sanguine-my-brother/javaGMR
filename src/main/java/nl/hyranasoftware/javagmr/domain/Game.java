/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.javagmr.controller.PlayerController;
import nl.hyranasoftware.javagmr.threads.RetrievePlayers;

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
    
    public void getPlayersFromGMR(){
        try {
            RetrievePlayers rp = new RetrievePlayers(players);
            ExecutorService executor = Executors.newFixedThreadPool(1);
            Future<List<Player>> future = executor.submit(rp);
            players = future.get();
            executor.shutdown();
        } catch (Exception ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    
    
    
    
}
