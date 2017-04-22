/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author danny_000
 */
public class GMRLogger {
    private final static Logger LOGGER = Logger.getLogger(GMRLogger.class.getName());
    
    public static void logLine(String line){
        LOGGER.log(Level.INFO, line);
    }
    
}
