/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.javagmr.controller.PlayerController;
import nl.hyranasoftware.javagmr.exception.InValidUserException;

/**
 *
 * @author danny_000
 */
public class JGMRConfig implements Serializable {

    String path;
    String authCode;
    String playerSteamId;
    private static JGMRConfig instance = null;

    protected JGMRConfig() {

    }

    public static JGMRConfig getInstance() {
        if (instance == null) {
            File configFile = new File("jGMR.config");
            try {
                if (configFile.exists()) {
                    ObjectMapper mapper = new ObjectMapper();
                    instance = mapper.readValue(configFile, JGMRConfig.class);
                } else {
                    instance = new JGMRConfig();
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
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) throws InValidUserException {
        PlayerController pc = new PlayerController();
        String playerCode = pc.getPlayerId(authCode);
        if (playerCode != null) {
            this.authCode = authCode;
            this.playerSteamId = playerCode;
            saveConfig();
        }else{
            
            throw new InValidUserException();
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
            File configFile = new File("jGMR.config");
            mapper.writeValue(configFile, this);
        } catch (IOException ex) {
            Logger.getLogger(JGMRConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

}