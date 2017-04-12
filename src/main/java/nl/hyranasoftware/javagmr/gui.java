/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import nl.hyranasoftware.javagmr.views.fxml.JgmrGuiController;

/**
 *
 * @author danny_000
 */
public class gui extends Application {

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = null;
        String url = null;
        url = getClass().getResource("views/fxml/jgmrGui.fxml").toString();
        System.out.println("  * url: " + url);
        loader = new FXMLLoader(getClass().getResource("views/fxml/jgmrGui.fxml"));
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scene scene = new Scene(root);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("eicon.png")));
        
        primaryStage.setTitle("Giant Multi Robot Java-Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
