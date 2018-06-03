/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.domain;

import java.util.Comparator;

/**
 *
 * @author padoura
 */
public class GameCompare implements Comparator<Game>{

    @Override
    public int compare(Game g1, Game g2) {
        return g1.getCurrentTurn().getStarted().toDate().compareTo(g2.getCurrentTurn().getStarted().toDate());
    }
    
}
