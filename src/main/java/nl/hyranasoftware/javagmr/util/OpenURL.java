/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.util;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.javagmr.views.fxml.JgmrGuiController;

/**
 *
 * @author danny_000
 */
public class OpenURL {

    public static void openUrlInBrowser(String url) {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("linux")) {
                Runtime.getRuntime().exec("xdg-open " + url);
            } else {
                java.awt.Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (IOException ex) {
            Logger.getLogger(OpenURL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
