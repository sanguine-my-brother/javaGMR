/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.controller.PlayerController;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.domain.Player;
import nl.hyranasoftware.javagmr.util.JGMRConfig;
import nl.hyranasoftware.javagmr.util.OpenURL;
import org.joda.time.DateTime;

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
    @FXML
    private ProgressBar pbDownload;

    private Game game;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vbGamePane.getChildren().remove(pbDownload);
        pbDownload.getStyleClass().add("success");
        pbDownload.applyCss();
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

    @FXML
    protected void uploadGame() {
        FileChooser directoryChooser = new FileChooser();
        if (JGMRConfig.getInstance().getPath() != null) {
            directoryChooser.setInitialDirectory(new File(JGMRConfig.getInstance().getPath()));
        }
        Stage stage = new Stage();
        File fileResult = directoryChooser.showOpenDialog(stage);
        if (fileResult != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Upload to " + game.getName());
            alert.setHeaderText("Upload to " + game.getName());
            alert.setContentText("Are you sure you wish to upload " + fileResult.getName() + " to the game " + game.getName());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                uploadGame(fileResult);
            }
        }
    }
    
    @FXML
    protected void viewGame(){
         String webURI = "http://multiplayerrobot.com/Game#" + game.getGameid();
         OpenURL.openUrlInBrowser(webURI);
    }

    @FXML
    protected void downloadGame() {

        GameController gc = new GameController() {
            @Override
            public void sendDownloadProgress(double percent) {
                updateDownloadProgressBar(percent);
            }
        };

        vbGamePane.getChildren().add(pbDownload);
        //Scene scene = ((VBox) vbGamePane.getParent()).getParent().getParent().getScene();
        Scene scene = vbGamePane.getScene();
        JgmrGuiController jgui = (JgmrGuiController) scene.getUserData();
        jgui.downloadGame(game);
        Task t = new Task() {
            @Override
            protected Object call() throws Exception {

                gc.downloadSaveFile(game);
                Platform.runLater(() -> {
                    vbGamePane.getChildren().remove(pbDownload);
                    TrayNotification downloadSucces = new TrayNotification("Download successful", "Go and conquer your enemies", Notifications.SUCCESS);
                    downloadSucces.setAnimation(Animations.POPUP);
                    downloadSucces.showAndDismiss(Duration.seconds(3));
                    jgui.resumeWatchService();
                });

                return null;
            }

        };

        Thread thread = new Thread(t);
        thread.start();

    }

    private void updateDownloadProgressBar(double size) {
        Platform.runLater(() -> {
            pbDownload.setProgress(size);
        });
    }

    private void getPlayers() {
        File cacheDirectory = new File("cache");
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdir();
        }
        Task<Void> getPlayersTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
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
                            if (DateTime.now().minusWeeks(1).isAfter(playerimage.lastModified())) {
                                
                            }
                        }

                        ImageView imageView = new ImageView();
                        Image image = new Image("file:" + playerimage.getAbsolutePath());
                        imageView.setPreserveRatio(true);
                        imageView.setFitHeight(25);
                        imageView.setFitWidth(25);

                        imageView.setImage(image);
                        Platform.runLater(() -> {
                            hbPlayers.getChildren().add(imageView);
                        });

                    } else {
                        ImageView imageView = new ImageView();
                        Image image = new Image(getClass().getResourceAsStream("computericon.png"));
                        imageView.setPreserveRatio(true);
                        imageView.setFitHeight(25);
                        imageView.setFitWidth(25);

                        imageView.setImage(image);
                        Platform.runLater(() -> {
                            
                            hbPlayers.getChildren().add(imageView);
                            
                        });

                    }
                }
                return null;
            }
        };
        Thread getPlayers = new Thread(getPlayersTask);
        getPlayers.setName(game.getName() + "_retrievingPlayers");
        getPlayers.start();
    }

    public void uploadGame(File file) {

        Platform.runLater(() -> {
            vbGamePane.getChildren().add(pbDownload);
            pbDownload.setProgress(-1.0f);
        });

        GameController gc = new GameController();
        if (file.exists()) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    boolean uploadStatusSucces = gc.uploadSaveFile(game, file);

                    Platform.runLater(() -> {
                        if (uploadStatusSucces) {
                            TrayNotification uploadSucces = new TrayNotification("Upload successful", "", Notifications.SUCCESS);
                            uploadSucces.setAnimation(Animations.POPUP);
                            uploadSucces.showAndDismiss(Duration.seconds(3));
                            Scene scene = vbGamePane.getScene();
                            JgmrGuiController jgui = (JgmrGuiController) scene.getUserData();
                            jgui.removeGameFromPlayerTurn(game);
                            ((VBox) vbGamePane.getParent()).getChildren().remove(vbGamePane);

                        } else {
                            try {
                                Dialog dg = new Dialog();
                                dg.setContentText("Either check on GMR if it's your turn or try uploading it again");
                                dg.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                dg.setTitle("Could not upload savefile");
                                dg.show();
                            } catch (Exception e) {
                                System.out.print(e);
                            }
                        }
                        vbGamePane.getChildren().remove(pbDownload);
                    });

                    return null;
                }
            };
            Thread t = new Thread(task);
            t.setName("Manuelupload");
            t.start();
        }

    }

    public Game getGame() {
        return game;
    }

    public void isAllGames() {
        btGamePage.getStyleClass().remove("first");
        lbTimeLeft.setPrefWidth(lbTimeLeft.getPrefWidth() + 130);
        hbGameInfo.getChildren().remove(btUpload);
        hbGameInfo.getChildren().remove(btDownload);

    }

}
