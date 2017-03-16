/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.controller;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import nl.hyranasoftware.javagmr.domain.Player;

/**
 *
 * @author danny_000
 */
public class PlayerController {
    
    public Player getPlayerFromGMR(String playerid) throws UnirestException{
        String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetGamesAndPlayers";
        String response = Unirest.get(requestUrl).queryString("playerIDText", playerid).queryString("authKey","").asJson().getBody().toString();
        Gson gson = new Gson();
        
        
        
        return null;
        
    }
    
}
