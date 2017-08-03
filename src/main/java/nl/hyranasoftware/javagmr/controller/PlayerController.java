/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import nl.hyranasoftware.javagmr.domain.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.util.JGMRConfig;

/**
 *
 * @author danny_000
 */
public class PlayerController {

    public Player getPlayerFromGMR(String playerid) {
        try {
            String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetGamesAndPlayers";
            String response = Unirest.get(requestUrl).queryString("playerIDText", playerid).queryString("authKey", "").asJson().getBody().toString();
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

    public void downloadPlayerAvatar(Player player) throws MalformedURLException, IOException {
        URL url = new URL(player.avatarUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
        long completeFileSize = httpConnection.getContentLength();
        httpConnection.setReadTimeout(15000);

        File targetFile = new File("cache/" + player.getSteamId() + ".jpg");
        java.io.BufferedInputStream is = new java.io.BufferedInputStream(httpConnection.getInputStream());

        try (OutputStream outStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            double downLoadFileSize = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                downLoadFileSize = downLoadFileSize + bytesRead;
                outStream.write(buffer, 0, bytesRead);
            }
        }

    }

    public List<Player> retrievePlayersFromGame(List<Player> players) {
        String playerIds = "";
        List<Player> retrievedPlayers = null;
        boolean firstPlayer = true;
        for (Player p : players) {
            if (!p.getSteamId().equals("0")) {
                if (firstPlayer) {
                    playerIds = playerIds + p.getSteamId();
                    firstPlayer = false;
                } else {
                    playerIds = playerIds + "_" + p.getSteamId();
                }
            }
        }
        try {
            String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetGamesAndPlayers";
            String response = Unirest.get(requestUrl).queryString("playerIDText", playerIds).queryString("authKey", "").asJson().getBody().toString();
            ObjectMapper mapper = new ObjectMapper();
            String playerNode = mapper.readTree(response).get("Players").toString();

            retrievedPlayers = mapper.readValue(playerNode, new TypeReference<List<Player>>() {
            });
        } catch (Exception ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        for (Player p : retrievedPlayers) {
            Player tempPlayer = players.get(players.indexOf(p));
            p.setTurnOrder(tempPlayer.getTurnOrder());
            players.set(players.indexOf(p), p);
        }
        return players;
    }

    public String getPlayerId(String authCode) {
        String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/AuthenticateUser";
        String response = null;
        try {
            response = Unirest.get(requestUrl).queryString("authKey", authCode).asString().getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (response != null) {
            if (!response.equals("null")) {
                return response;
            }
        }
        return null;
    }

}
