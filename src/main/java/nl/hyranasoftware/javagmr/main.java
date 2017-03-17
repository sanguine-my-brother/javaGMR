/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr;

import java.io.IOException;
import java.util.List;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.domain.Game;

/**
 *
 * @author danny_000
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //PlayerController pc = new PlayerController();
        //Player player = pc.getPlayerFromGMR("76561198037737017");
        GameController gc = new GameController();
        List<Game> games = gc.getGames();
    }
    
}
