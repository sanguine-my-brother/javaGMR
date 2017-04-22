/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.Objects;

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

    public Player(String steamId, String PersonaName, String avatarUrl, int PersonaState, int GameID) {

        this.steamId = steamId;
        this.personaName = PersonaName;
        this.avatarUrl = avatarUrl;
        this.PersonaState = PersonaState;
        this.gameId = gameId;
    }
    
    
    
    public String getSteamId() {
        return steamId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
    @JsonProperty("PersonaState")
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

    public void setTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.steamId);
        hash = 47 * hash + Objects.hashCode(this.personaName);
        return hash;
    }
    
    
    
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        
        final Player other = (Player) obj;
        if (!Objects.equals(this.steamId, other.steamId)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
}
