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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.gui;

/**
 * FXML Controller class
 *
 * @author danny_000
 */
public class JgmrGuiController implements Initializable {

    @FXML
    private Button btSettings;

    @FXML
    private ListView lvPlayerTurnGames;
    @FXML
    private ListView lvAllGames;
    @FXML
    private AnchorPane apAbove;

    ContextMenu cm = new ContextMenu();
    ObservableList<Game> currentGames;
    ObservableList<Game> playerGames;
    
    GameController gc = new GameController();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("  * jdskfhksdhf: " + url);
        currentGames = FXCollections.observableArrayList(gc.getGames());
        lvAllGames.setItems(currentGames);
        playerGames = FXCollections.observableArrayList(gc.retrievePlayersTurns(currentGames));
        lvPlayerTurnGames.setItems(playerGames);
        initializeContextMenu();
        initializeListViews();

    }

    @FXML
    private void settingsButton() {
        FXMLLoader loader = null;
        String url = null;
        url = getClass().getResource("settingsDialog.fxml").toString();
        System.out.println("  * url: " + url);
        loader = new FXMLLoader(getClass().getResource("settingsDialog.fxml"));
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage dialog = new Stage();
        Scene scene = new Scene(root);
        dialog.setTitle("Giant Multi Robot Java-Client");
        dialog.setScene(scene);
        dialog.show();
    }

    private void initializeListViews() {
        Group root = new Group();


 
        lvAllGames.setCellFactory(new Callback<ListView<Game>, ListCell<Game>>() {
            @Override
            public ListCell<Game> call(ListView<Game> param) {
                ListCell<Game> cell = new ListCell<Game>() {
                    @Override
                    protected void updateItem(Game g, boolean b) {
                        super.updateItem(g, b);
                        if (g != null) {
                            setText(g.getName());
                        }
                    }
                };
                return cell;
            }

        });
        lvPlayerTurnGames.setCellFactory(new Callback<ListView<Game>, ListCell<Game>>() {
            @Override
            public ListCell<Game> call(ListView<Game> param) {
                ListCell<Game> cell = new ListCell<Game>() {
                    @Override
                    protected void updateItem(Game g, boolean b) {
                        super.updateItem(g, b);
                        if (g != null) {
                            setText(g.getName());
                        }
                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(cell.getText().length() > 0){
                        if(event.getButton() == MouseButton.PRIMARY)
                            cm.show(cell, event.getScreenX(), event.getScreenY());
                        }
                    }
                    
                });
                cell.setContextMenu(cm);
                return cell;
            }

        });
    }

    private void initializeContextMenu() {
        MenuItem downloadSaveFile = new MenuItem("Download Save File");
        downloadSaveFile.setOnAction(event -> {
            gc.downloadSaveFile((Game) lvPlayerTurnGames.getSelectionModel().getSelectedItem());
            Dialog dialog = new Dialog();
            dialog.setContentText("The save file has succesfully been downloaded");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.setTitle("Onward my noble Leader and conquer thy enemies");
            dialog.show();
        });
        cm.getItems().addAll(downloadSaveFile);

    }

}
