/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import nl.hyranasoftware.javagmr.domain.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 *
 * @author danny_000
 */
public class PlayerController {
    
    public Player getPlayerFromGMR(String playerid) throws UnirestException, IOException{
        String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetGamesAndPlayers";
        String response = Unirest.get(requestUrl).queryString("playerIDText", playerid).queryString("authKey","").asJson().getBody().toString();
        ObjectMapper mapper = new ObjectMapper();
        String playerNode = mapper.readTree(response).get("Players").get(0).toString();

        Player player = mapper.readValue(playerNode, Player.class);
        
        return player;
        
    }
    
}
