/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.util.JGMRConfig;

/**
 *
 * @author danny_000
 */
public class GameController {

    public List<Game> getGames() {
        try {
            String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetGamesAndPlayers?playerIDText";
            String response = Unirest.get(requestUrl).queryString("playerIDText", "").queryString("authKey", JGMRConfig.getInstance().getAuthCode()).asJson().getBody().toString();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            String gamesNode = mapper.readTree(response).get("Games").toString();
            List<Game> games = mapper.readValue(gamesNode, new TypeReference<List<Game>>() {
            });
            class PlayersTask implements Runnable {

                List<Game> games;

                PlayersTask(List<Game> s) {
                    games = s;
                }

                public void run() {
                    for (Game g : games) {
                        g.getPlayersFromGMR();
                    }
                }
            }
            Thread t = new Thread(new PlayersTask(games));
            t.start();
            return games;
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnirestException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public List<Game> retrievePlayersTurns(List<Game> games){
        List<Game> playerTurns = new ArrayList<Game>();
        for(Game g : games){
            if(g.getCurrentTurn().getUserId().equals(JGMRConfig.getInstance().getPlayerSteamId())){
                playerTurns.add(g);
            }
        }
        
        
        return playerTurns;
    }

    public void downloadSaveFile(Game selectedItem) {
        String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetLatestSaveFileBytes";
        InputStream is = null;
        try {
            is = Unirest.get(requestUrl).queryString("authKey", JGMRConfig.getInstance().getAuthCode()).queryString("gameId", selectedItem.getGameid()).asBinary().getRawBody();
            File targetFile = new File(JGMRConfig.getInstance().getPath() + "/(jGMR) Play this one.Civ5Save");
            OutputStream outStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
        }
        } catch (Exception ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void uploadSaveFile(Game selectedItem){
        
    }

}
