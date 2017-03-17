/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import nl.hyranasoftware.javagmr.domain.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.javagmr.domain.Game;

/**
 *
 * @author danny_000
 */
public class PlayerController {
    
    public Player getPlayerFromGMR(String playerid){
        try {
            String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetGamesAndPlayers";
            String response = Unirest.get(requestUrl).queryString("playerIDText", playerid).queryString("authKey","").asJson().getBody().toString();
            ObjectMapper mapper = new ObjectMapper();
            String playerNode = mapper.readTree(response).get("Players").get(0).toString();
            
            Player player = mapper.readValue(playerNode, Player.class);
            
            return player;
        } catch (UnirestException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public List<Player> retrievePlayersFromGame(List<Player> players){
        String playerIds = "";
        List<Player> retrievedPlayers = null;
        boolean firstPlayer = true;
        for(Player p : players){
            if(!p.getSteamId().equals("0")){
            if(firstPlayer){
                playerIds = playerIds + p.getSteamId();
                firstPlayer = false;
            }else{
                playerIds = playerIds + "_" + p.getSteamId();
            }
            }
        }
        try {
            String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetGamesAndPlayers";
            String response = Unirest.get(requestUrl).queryString("playerIDText", playerIds).queryString("authKey","").asJson().getBody().toString();
            ObjectMapper mapper = new ObjectMapper();
            String playerNode = mapper.readTree(response).get("Players").toString();
            
            retrievedPlayers = mapper.readValue(playerNode, new TypeReference<List<Player>>(){});
        } catch (Exception ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        for(Player p : retrievedPlayers){
            Player tempPlayer = players.get(players.indexOf(p));
            p.setTurnOrder(tempPlayer.getTurnOrder());
            players.set(players.indexOf(p), p);
        }
        return players;
    }
    
    
}
