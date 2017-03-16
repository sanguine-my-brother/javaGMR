/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

/**
 *
 * @author danny_000
 */
public class Player {
    
    int steamId;
    String personalname;
    String avatarUrl;
    int PersonaState;
    int gameId;

    public Player() {
    }

    public Player(int steamId, String personalname, String avatarUrl, int PersonaState, int gameId) {
        this.steamId = steamId;
        this.personalname = personalname;
        this.avatarUrl = avatarUrl;
        this.PersonaState = PersonaState;
        this.gameId = gameId;
    }
    
    
    public int getSteamId() {
        return steamId;
    }

    public String getPersonalname() {
        return personalname;
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
    
    
    
}
