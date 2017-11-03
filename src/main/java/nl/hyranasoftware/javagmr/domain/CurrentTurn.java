/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.joda.time.DateTime;

/**
 *
 * @author danny_000
 */
public class CurrentTurn {
    @JsonProperty("TurnId")
    int turnId;
    @JsonProperty("Number")
    int number;
    @JsonProperty("UserId")
    String userId;
    @JsonProperty("Started")
    DateTime started;
    @JsonProperty("Expires")
    DateTime expires;
    @JsonProperty("PlayerNumber")
    int playerNumber;
    @JsonProperty("Skipped")
    boolean skipped;
    @JsonProperty("IsFirstTurn")
    boolean isfirstTurn;

    public int getTurnId() {
        return turnId;
    }

    public int getNumber() {
        return number;
    }

    public String getUserId() {
        return userId;
    }

    public DateTime getStarted() {
        return started;
    }

    public DateTime getExpires() {
        return expires;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public boolean isIsfirstTurn() {
        return isfirstTurn;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.turnId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        final CurrentTurn otherTurn = (CurrentTurn) obj;
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        if (this.number != otherTurn.number){
            return false;
        }
        return true;
    }
    
    
    
    
    
}
