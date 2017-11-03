/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author danny_000
 */
public class GMRLogger {

    private final static Logger LOGGER = Logger.getLogger("GMR Logger");

    public static void logLine(String line) {
        FileHandler fh = null;
        if (JGMRConfig.getInstance().isLogToFile()) {
            try {
                fh = new FileHandler("logfile.txt");
                LOGGER.addHandler(fh);

            } catch (IOException ex) {
                Logger.getLogger(GMRLogger.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(GMRLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        LOGGER.log(Level.INFO, line);
        if (fh != null) {
            fh.close();
        }
    }

}
