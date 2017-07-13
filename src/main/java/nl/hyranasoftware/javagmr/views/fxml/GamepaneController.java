/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nl.hyranasoftware.javagmr.controller.PlayerController;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.domain.Player;

/**
 * FXML Controller class
 *
 * @author danny_000
 */
public class GamepaneController implements Initializable {

    @FXML
    private HBox hbGameInfo;
    @FXML
    private VBox vbGamePane;
    @FXML
    private Label lbGameName;
    @FXML
    private Label lbTimeLeft;
    @FXML
    private Button btGamePage;
    @FXML
    private Button btUpload;
    @FXML
    private Button btDownload;
    @FXML
    private HBox hbPlayers;

    Game game;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void constructView(Game g) {
        this.game = g;
        lbGameName.setText(g.getName());
        lbTimeLeft.setText(g.getPrettyTimeLeft());
        getPlayers();
    }

    public VBox getVbGamePane() {
        return vbGamePane;
    }

    private void getPlayers() {
        File cacheDirectory = new File("cache");
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdir();
        }
        Thread getPlayers = new Thread(new Runnable() {
            @Override
            public void run() {
                game.sortPlayers();
                PlayerController pc = new PlayerController();
                for (Player p : game.getPlayers()) {
                    if (!p.getSteamId().equals("0")) {
                        File playerimage = new File("cache/" + p.getSteamId() + ".jpg");
                        if (!playerimage.exists()) {
                            Player gmrPlayer = pc.getPlayerFromGMR(p.getSteamId());
                            try {
                                pc.downloadPlayerAvatar(gmrPlayer);
                            } catch (IOException ex) {
                                Logger.getLogger(GamepaneController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        System.out.println(playerimage.getPath());
                        ImageView imageView = new ImageView();
                        Image image = new Image("file:" + playerimage.getAbsolutePath());
                        imageView.maxHeight(25);
                        imageView.setPreserveRatio(false);
                        
                        
                        imageView.setImage(image);
                        Platform.runLater(() ->{
                            hbPlayers.getChildren().add(imageView);
                        });
                        
                    }
                }
            }

        });
        getPlayers.setName(game.getName() + "_retrievingPlayers");
        getPlayers.start();
    }

}
