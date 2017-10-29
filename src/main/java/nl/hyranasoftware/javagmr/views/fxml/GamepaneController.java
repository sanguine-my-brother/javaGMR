/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.controller.PlayerController;
import nl.hyranasoftware.javagmr.domain.CurrentTurn;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.domain.Note;
import nl.hyranasoftware.javagmr.domain.Player;
import nl.hyranasoftware.javagmr.util.GMRLogger;
import nl.hyranasoftware.javagmr.util.JGMRConfig;
import nl.hyranasoftware.javagmr.util.OpenURL;
//import org.controlsfx.glyphfont.FontAwesome;
//import org.controlsfx.glyphfont.GlyphFont;
//import org.controlsfx.glyphfont.GlyphFontRegistry;
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
    private Button btNoteEditor;
    @FXML
    private Button btUpload;
    @FXML
    private Button btDownload;
    @FXML
    private HBox hbPlayers;
    @FXML
    private ProgressBar pbDownload;

    private Game game;
    private CurrentTurn currentTurn;
    private int currentTurnPlayerNumber;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vbGamePane.getChildren().remove(pbDownload);
        pbDownload.getStyleClass().add("success");
        pbDownload.applyCss();

        btDownload.setTooltip(new Tooltip("Download game"));
        btUpload.setTooltip(new Tooltip("Upload game"));
        btNoteEditor.setTooltip(new Tooltip("Open notes editor"));
        btGamePage.setTooltip(new Tooltip("Go to the GMR page of this game"));
        //GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        // FontAwesome.Glyph.
        //btDownload.setGraphic(fontAwesome.create(FontAwesome.Glyph.DOWNLOAD));
        //btDownload.setText(.getText());
        //Text fontAwesomeIcon = FontAwesomeIconFactory.get();

    }

    public void constructView(Game g) {
        this.game = g;
        this.currentTurn = g.getCurrentTurn();
        currentTurnPlayerNumber = this.game.getCurrentTurn().getPlayerNumber();
        lbGameName.setText(g.getName());
        lbTimeLeft.setText(g.getPrettyTimeLeft());
        if(hasNote()){
            btNoteEditor.getStyleClass().add("primary");
        }
        getPlayers();
        lbTimeLeft.setTooltip(new Tooltip(g.getPrettyTimeStarted()));

    }

    public void refreshTime() {

        Platform.runLater(() -> {
            lbTimeLeft.setText(game.getPrettyTimeLeft());
        });
    }

    public void refreshPlayers(Game g) {
        GMRLogger.logLine("Refreshing players on: " + g.getName());
        this.game = g;
        this.currentTurnPlayerNumber = g.getCurrentTurn().getPlayerNumber();
        Platform.runLater(() -> {
            hbPlayers.getChildren().clear();

        });
        getPlayers();

    }

    public VBox getVbGamePane() {
        return vbGamePane;
    }

    @FXML
    protected void uploadGame() {
        if (!new File(JGMRConfig.getInstance().getPath() + "/.jgmrlock.lock").exists()) {
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
        } else {
            Dialog dg = new Dialog();
            dg.setContentText("An upload or download is already in progress, please wait for the previous operation to finish.");
            dg.setTitle("Download or Upload already in progress.");
            dg.getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
            Platform.runLater(() -> {
                dg.showAndWait();
            });
        }
    }

    @FXML
    protected void viewGame() {
        String webURI = "http://multiplayerrobot.com/Game#" + game.getGameid();
        OpenURL.openUrlInBrowser(webURI);
    }

    @FXML
    protected void downloadGame() {
        if (game.getCurrentTurn().isIsfirstTurn()) {
            Dialog dg = new Dialog();
            dg.setContentText("Congratulations, you get to make a new game. Please create a new Hotseat game in Civ 5 and than press the upload button. (It's next to the download button)\nIf you need more information about this game press the most left button, it will take you directly to the game page");

            dg.setTitle("New Game");

            dg.getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
            Platform.runLater(() -> {
                dg.showAndWait();
            });
        } else {
            GameController gc = new GameController() {
                @Override
                public void sendDownloadProgress(double percent) {
                    updateDownloadProgressBar(percent);
                }
            };
            Scene scene = vbGamePane.getScene();
            JgmrGuiController jgui = (JgmrGuiController) scene.getUserData();
            jgui.downloadGame(game);
            if (!new File(JGMRConfig.getInstance().getPath() + "/.jgmrlock.lock").exists()) {
                vbGamePane.getChildren().add(pbDownload);
                //Scene scene = ((VBox) vbGamePane.getParent()).getParent().getParent().getScene();
                Task t = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        gc.downloadSaveFile(game);
                        Platform.runLater(() -> {
                            pbDownload.setProgress(0);
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
            } else {
                Dialog dg = new Dialog();
                dg.setContentText("An upload or download is already in progress, please wait for the previous operation to finish.");
                dg.setTitle("Download or Upload already in progress.");
                dg.getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
                Platform.runLater(() -> {
                    dg.showAndWait();
                });
            }
        }
    }

    private Scene getScene(String fxml) {
        FXMLLoader loader = null;
        String url = null;
        url = getClass().getResource(fxml).toString();
        loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GamepaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Object controller = loader.getController();
        Scene scene = new Scene(root);
        scene.setUserData(controller);
        return scene;
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
            t.setName("Uploading: " + game.getName());
            t.start();

        }

    }

    public Game getGame() {
        return game;
    }

    public void isAllGames() {
        //btGamePage.getStyleClass().remove("first");
        btNoteEditor.getStyleClass().add("last");
        lbTimeLeft.setPrefWidth(lbTimeLeft.getPrefWidth() + 70);
        hbGameInfo.getChildren().remove(btUpload);
        hbGameInfo.getChildren().remove(btDownload);
        

    }

    @FXML
    public void openEditor() {
        boolean didExist = false;
        File noteFile = new File("notes/" + this.game.getGameid() + ".json");
        try {
            Note note = null;
            if (noteFile.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                note = mapper.readValue(noteFile, Note.class);
            } else {
                note = new Note(this.game.getGameid());
                note.setText(" ");
                didExist = true;
            }
            Scene scene = this.getScene("texteditor.fxml");
            TexteditorController tec = (TexteditorController) scene.getUserData();
            tec.constructView(note);
            Stage editor = new Stage();
            editor.setOnHiding(event -> {
                tec.saveOnExit();
            });
            editor.setScene(scene);
            editor.showAndWait();
            if(hasNote() && didExist){
                btNoteEditor.getStyleClass().add("primary");
                 btNoteEditor.applyCss();
            }
        } catch (Exception ex) {

        }
    }

    public int getCurrentTurnPlayerNumber() {
        return currentTurnPlayerNumber;
    }

    public void setCurrentTurnPlayerNumber(int currentTurnNumber) {
        this.currentTurnPlayerNumber = currentTurnNumber;
    }
    
    private boolean hasNote(){
        File noteFile = new File("notes/" + this.game.getGameid() + ".json");
        return noteFile.exists();
    }

}
