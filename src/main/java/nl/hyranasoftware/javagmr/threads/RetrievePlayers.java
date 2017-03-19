/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.threads;

import java.util.List;
import java.util.concurrent.Callable;
import nl.hyranasoftware.javagmr.domain.Player;
import nl.hyranasoftware.javagmr.controller.PlayerController;

/**
 *
 * @author danny_000
 */
public class RetrievePlayers implements Callable {

    PlayerController pc = new PlayerController();
    List<Player> players;
    public RetrievePlayers(List<Player> players){
        this.players = players; 
    }

    public List<Player> call() throws Exception {
        return pc.retrievePlayersFromGame(players);
    }

    
    
    
}
