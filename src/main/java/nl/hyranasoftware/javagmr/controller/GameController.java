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
import java.io.IOException;
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

}
