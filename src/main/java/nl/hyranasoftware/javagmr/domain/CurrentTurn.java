/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

import org.joda.time.DateTime;

/**
 *
 * @author danny_000
 */
public class CurrentTurn {
    int turnId;
    int number;
    String userId;
    DateTime started;
    DateTime expires;
    Player player;
    boolean skipped;
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

    public Player getPlayer() {
        return player;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public boolean isIsfirstTurn() {
        return isfirstTurn;
    }
    
    
    
}
