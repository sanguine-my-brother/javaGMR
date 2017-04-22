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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.util.GMRLogger;
import nl.hyranasoftware.javagmr.util.JGMRConfig;
import org.joda.time.DateTime;

/**
 *
 * @author danny_000
 */
public class GameController {

    /**
     * Gets the games from the GMR sites and returns a List of all the games the
     * player is involved with Including games where it is NOT the player's turn
     */
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
            Collections.sort(games);
            return games;
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnirestException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method will return all the games where it is the players turn It
     * will also look in the JGMRConfig singleton to check for recent uploaded
     * games. If it find such a game it will not be returned in this list The
     * wait time for turns is 15 minutes.
     *
     * @param games Insert here the list of all the games the player is involved
     * with
     * @return returns a list of the games where it is the player's turn
     *
     */
    public List<Game> retrievePlayersTurns(List<Game> games) {
        List<Game> playerTurns = new ArrayList<Game>();
        for (Game g : games) {
            if (g.getCurrentTurn().getUserId().equals(JGMRConfig.getInstance().getPlayerSteamId())) {

                if (JGMRConfig.getInstance().getUploadedGames().contains(g)) {
                    if (JGMRConfig.getInstance().getUploadedGames().get(JGMRConfig.getInstance().getUploadedGames().indexOf(g)).getUploaded().isAfter(DateTime.now().plusMinutes(15))) {
                        JGMRConfig.getInstance().uploadedGameExpired(g);
                        playerTurns.add(g);
                    }
                } else {
                    playerTurns.add(g);
                }
            }
        }

        Collections.sort(playerTurns);
        Collections.reverse(playerTurns);
        return playerTurns;
    }

    /**
     * Downloads the save file from the GMR site.
     *
     * @param selectedItem This parameter is used to download the save file from
     * the site
     */
    public void downloadSaveFile(Game selectedItem) throws MalformedURLException, IOException {
        String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/GetLatestSaveFileBytes";
        URL url = new URL(requestUrl + "?authkey=" + JGMRConfig.getInstance().getAuthCode() + "&gameId=" + selectedItem.getGameid());
        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
        long completeFileSize = httpConnection.getContentLength();
        httpConnection.setReadTimeout(15000);

        File targetFile = new File(JGMRConfig.getInstance().getPath() + "/(jGMR) Play this one.Civ5Save");
        java.io.BufferedInputStream is = new java.io.BufferedInputStream(httpConnection.getInputStream());

        try (OutputStream outStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            double downLoadFileSize = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                downLoadFileSize = downLoadFileSize + bytesRead;
                outStream.write(buffer, 0, bytesRead);
                sendDownloadProgress(((double) downLoadFileSize / (double) completeFileSize));
            }
            JGMRConfig.getInstance().readDirectory();
        }

    }

    /**
     * Uploads the save file to the GMR site The game parameter is used to
     * identify to which game the savefile should be uploaded to the GMR site
     * The filename parameter is used to identify the file you want to upload.
     * Returns a true value if the upload was successful
     */
    public boolean uploadSaveFile(Game game, String filename) {
        try {
            return doUpload(new FileInputStream(new File(JGMRConfig.getInstance().getPath() + "/" + filename)), game);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Uploads the save file to the GMR site The game parameter is used to
     * identify to which game the savefile should be uploaded to the GMR site
     * The file parameter is used to identify the file you want to upload.
     * Returns a true value if the upload was successful
     *
     * @param game used to identify to where the save file should be uploaded
     * @param file used to identify the file you want to upload to GMR
     */
    public boolean uploadSaveFile(Game game, File file) {
        GMRLogger.logLine("uploadSave manuel");
        try {
            return doUpload(new FileInputStream(file), game);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private boolean doUpload(InputStream stream, Game game) {
        String requestUrl = "http://multiplayerrobot.com/api/Diplomacy/SubmitTurn";
        try {

            int available = stream.available();       
            final byte bytes[] = new byte[available];
            
            while (stream.read(bytes) > 0) {

            }

            stream.close();
 
            Unirest.setTimeouts(10000, 15000);
            String result = Unirest.post(requestUrl)
                    .queryString("authKey", JGMRConfig.getInstance().getAuthCode())
                    .queryString("turnId", game.getCurrentTurn().getTurnId())
                    .body(bytes)
                    .asJson().getBody().toString();

            ObjectMapper mapper = new ObjectMapper();
            String gamesNode = mapper.readTree(result).get("ResultType").toString();
            int resultType = mapper.readValue(gamesNode, int.class);
            if (resultType == 1) {
                JGMRConfig.getInstance().readDirectory();
                JGMRConfig.getInstance().addUploadedGame(game);
                return true;
            }
            game.setUploaded(DateTime.now());

            GMRLogger.logLine(result);
            return false;

        } catch (UnirestException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    /**
     * This sends the progress in percentage gets overridden
     *
     * @param percent, the number of how much percent has been downloaded
     * already
     */
    public void sendDownloadProgress(double percent) {
        throw new UnsupportedOperationException();
        /*
        THIS MUST BE OVERRIDDEN BY YOUR VIEW
         */

    }

}
