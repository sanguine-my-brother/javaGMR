/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 *
 * @author danny_000
 */
public class Player {
    
    @JsonProperty("SteamID")
    public String steamId;
    @JsonProperty("PersonaName")
    public String personaName;
    @JsonProperty("AvatarUrl")
    public String avatarUrl;
    @JsonProperty("PersonaState")
    public int PersonaState;
    @JsonProperty("GameID")
    public int gameId;
    @JsonProperty("TurnOrder")
    int turnOrder;

    public Player() {
    }

    public Player(String steamId, String personalname, String avatarUrl, int PersonaState, int gameId) {

        this.steamId = steamId;
        this.personaName = personalname;
        this.avatarUrl = avatarUrl;
        this.PersonaState = PersonaState;
        this.gameId = gameId;
    }
    
    
    
    public String getSteamId() {
        return steamId;
    }

    public String getPersonalname() {
        return personaName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getPersonaState() {
        return PersonaState;
    }

    public int getGameId() {
        return gameId;
    }

    public String getPersonaName() {
        return personaName;
    }

    public int getTurnOrder() {
        return turnOrder;
    }
    @JsonSetter("UserId")
    public void setUserId(String userId){
        this.steamId = userId;
    }
    
    
    
}
