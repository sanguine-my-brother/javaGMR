/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.controller;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author padoura
 */
public class GameControllerTest {
    
    public GameControllerTest() {
    }

    /**
     * Test of editFilenameIfInvalid method, of class GameController.
     */
    @Test
    public void testEditFilenameIfInvalid() {
        System.out.println("editFilenameIfInvalid");
        String filename = "22 Player, Giant Earth Map: - \"The World Wars\".";
        GameController instance = new GameController();
        String expResult = "22 Player, Giant Earth Map  -  The World Wars";
        String result = instance.editFilenameIfInvalid(filename);
        assertEquals(expResult, result);
        
        filename = "Padoura Training Skirra: Game #1";
        instance = new GameController();
        expResult = "Padoura Training Skirra  Game #1";
        result = instance.editFilenameIfInvalid(filename);
        assertEquals(expResult, result);
        
    }
    
}
