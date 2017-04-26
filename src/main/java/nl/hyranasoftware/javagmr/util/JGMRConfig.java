/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.javagmr.controller.PlayerController;
import nl.hyranasoftware.javagmr.domain.Game;
import org.joda.time.DateTime;

/**
 *
 * @author danny_000
 */
public class JGMRConfig {

    private String path;
    private String authCode;
    private String playerSteamId;
    private int notificationFrequency = 15;
    private boolean notificationsMinized = true;
    private boolean minimizeToTray = true;
    private List<Game> uploadedGames = new ArrayList();

    @JsonIgnore
    List<SaveFile> saveFiles = new ArrayList();
    private static JGMRConfig instance = null;

    protected JGMRConfig() {

    }

    public static JGMRConfig getInstance() {
        if (instance == null) {
            File configFile = new File("jGMR.config");
            try {
                if (configFile.exists()) {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JodaModule());
                    instance = mapper.readValue(configFile, JGMRConfig.class);
                } else {
                    instance = new JGMRConfig();
                    instance.notificationFrequency = 15;
                }
            } catch (Exception ex) {
                Logger.getLogger(JGMRConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        saveConfig();
        readDirectory();
    }

    public int getNotificationFrequency() {
        return notificationFrequency;
    }

    public void setNotificationFrequency(int notificationFrequency) {
        this.notificationFrequency = notificationFrequency;
        saveConfig();
    }

    public boolean isNotificationsMinized() {
        return notificationsMinized;
    }

    public void setNotificationsMinized(boolean notificationsMinized) {
        this.notificationsMinized = notificationsMinized;
        saveConfig();
    }

    public boolean isMinimizeToTray() {
        return minimizeToTray;

    }

    public void setMinimizeToTray(boolean minimizeToTray) {
        this.minimizeToTray = minimizeToTray;
        saveConfig();
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        PlayerController pc = new PlayerController();
        String playerCode = pc.getPlayerId(authCode);
        if (playerCode != null) {
            this.authCode = authCode;
            this.playerSteamId = playerCode;
            saveConfig();
        } 

    }

    public String getPlayerSteamId() {
        return playerSteamId;
    }

    public void setPlayerSteamId(String playerSteamId) {
        this.playerSteamId = playerSteamId;
    }

    private void saveConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            File configFile = new File("jGMR.config");
            mapper.writeValue(configFile, this);
        } catch (IOException ex) {
            Logger.getLogger(JGMRConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readDirectory() {
        File directory = new File(this.path);
        saveFiles.clear();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    SaveFile saveFile = new SaveFile(files[i].getAbsolutePath());
                    saveFiles.add(saveFile);
                }
            }
        } else {
            this.path = null;
            saveConfig();
        }
    }

    public boolean didSaveFileChange(SaveFile saveFile) {
        if (saveFile.getSize() > 100 && !saveFiles.isEmpty() && saveFiles.indexOf(saveFile) != -1) {
            SaveFile retrievedFile = saveFiles.get(saveFiles.indexOf(saveFile));
            if (retrievedFile != null) {
                if (saveFile.getSize() > (retrievedFile.getSize() + 30) || saveFile.getSize() > 0 && saveFile.getSize() != 0 || saveFile.getSize() < (retrievedFile.getSize() - 30)) {
                    if ( !retrievedFile.getLastTimeModified().equals(saveFile.getLastTimeModified())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Game> getUploadedGames() {
        return uploadedGames;
    }

    public void addUploadedGame(Game game) {
        game.setUploaded(DateTime.now());
        this.uploadedGames.add(game);
        this.saveConfig();
    }

    @JsonIgnore
    public void uploadedGameExpired(Game game) {
        this.uploadedGames.remove(game);
    }

    public void setUploadedGames(List<Game> uploadedGames) {
        this.uploadedGames = uploadedGames;
    }

}
