/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import nl.hyranasoftware.javagmr.gui;

/**
 * FXML Controller class
 *
 * @author danny_000
 */
public class JgmrGuiController implements Initializable {

    @FXML
    private Button btSettings;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("  * jdskfhksdhf: " + url);
    }

    @FXML
    private void settingsButton(){
                FXMLLoader loader = null;
        String url = null;
        url  = getClass().getResource("settingsDialog.fxml").toString();
        System.out.println( "  * url: " + url );
        loader = new FXMLLoader(getClass().getResource("settingsDialog.fxml"));
        Parent root = null;
        try {
            root = (Parent)loader.load();
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage dialog = new Stage();
        Scene scene = new Scene(root); 
        dialog.setTitle("Giant Multi Robot Java-Client");
        dialog.setScene(scene);
        dialog.show();
    }
    
}
