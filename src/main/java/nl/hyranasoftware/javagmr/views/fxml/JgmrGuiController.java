/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import com.mashape.unirest.http.exceptions.UnirestException;
import dorkbox.systemTray.SystemTray;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.hyranasoftware.githubupdater.GithubUtility;
import nl.hyranasoftware.githubupdater.domain.Release;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.gui;
import nl.hyranasoftware.javagmr.threads.WatchDirectory;
import nl.hyranasoftware.javagmr.util.JGMRConfig;
import nl.hyranasoftware.javagmr.util.OpenURL;
import nl.hyranasoftware.javagmr.util.SaveFile;

/**
 * FXML Controller class
 *
 * @author danny_000
 */

/* https://github.com/PlusHaze/TrayNotification 
    Add this lib
    https://github.com/dorkbox/SystemTray
 */
public class JgmrGuiController implements Initializable {

    @FXML
    private Button btSettings;
    @FXML
    private VBox jgmrVbox;
    @FXML
    private VBox vbPlayerTurnBox;
    @FXML
    private HBox hbUpload;
    @FXML
    private HBox hbDownload;
    @FXML
    private ScrollPane gamesPane;
    @FXML
    private ListView lvAllGames;
    @FXML
    private AnchorPane apAbove;
    @FXML
    private Label lbTime;
    @FXML
    private ProgressBar pbDownload;
    @FXML
    private ProgressBar pbUpload;

    ContextMenu cm = new ContextMenu();
    boolean newDownload;
    WatchDirectory wd;
    Thread wdt;
    ObservableList<Game> currentGames = FXCollections.observableArrayList();
    ObservableList<Game> playerGames = FXCollections.observableArrayList();
    ChoiceDialog<Game> newSaveFileDialog;
    TrayNotification notification;
    SystemTray systemTray;
    Timeline notificationTimeline;
    int timeLeft;

    GameController gc;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = new GameController() {
            @Override
            public void sendDownloadProgress(double percent) {
                updateDownloadProgressBar(percent);
            }
        };

        checkForUpdates();
        initializeChoiceDialog();
        jgmrVbox.getChildren().remove(hbDownload);
        jgmrVbox.getChildren().remove(hbUpload);
        initializeListViews();
        if (JGMRConfig.getInstance().getPlayerSteamId() != null) {
            new Timeline(new KeyFrame(
                    Duration.seconds(2),
                    ae -> pullGames()))
                    .play();
        } else {
            lbTime.setText("Please enter your authcode in the settings");
            timeLeft = 60;
            pullGames();
        }
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.minutes(1),
                ae -> pullGames()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Timeline labelUpdater = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> updateLabel()));
        labelUpdater.setCycleCount(Timeline.INDEFINITE);
        labelUpdater.play();
        new Timeline(new KeyFrame(
                Duration.seconds(2),
                ae -> initializeSystemtray()))
                .play();
        initializeNotifications();

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
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Object controller = loader.getController();
        Scene scene = new Scene(root);
        scene.setUserData(controller);
        return scene;
    }

    private void initializeChoiceDialog() {
        newSaveFileDialog = new ChoiceDialog<>(null, playerGames);
        newSaveFileDialog.setTitle("Save file");
        newSaveFileDialog.setHeaderText("I see you played your turn");
        ((Stage) newSaveFileDialog.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        newSaveFileDialog.setContentText("Choose the game you would to submit to GMR");
    }

    private void pullGames() {
        if (JGMRConfig.getInstance().getPlayerSteamId() != null) {
            lbTime.setText("Retrieving game list from GMR... Please wait");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Game> retrievedGames = FXCollections.observableArrayList(gc.getGames());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            currentGames.clear();
                            currentGames = FXCollections.observableArrayList(retrievedGames);
                            lvAllGames.setItems(currentGames);

                            playerGames = FXCollections.observableArrayList(gc.retrievePlayersTurns(currentGames));
                            vbPlayerTurnBox.getChildren().clear();
                            boolean second = false;
                            for (Game g : playerGames) {
                                Stage dialog = new Stage();
                                Scene scene = getScene("gamepane.fxml");
                                GamepaneController gpc = (GamepaneController) scene.getUserData();
                                gpc.constructView(g);
                                if (g.getName().toLowerCase().contains("theme")) {
                                    gpc.getVbGamePane().getStyleClass().add("gmrleaguegame");
                                    gpc.getVbGamePane().applyCss();
                                } else {
                                    if (second) {
                                        gpc.getVbGamePane().getStyleClass().add("gameitemsecond");
                                        gpc.getVbGamePane().applyCss();
                                        second = false;
                                    } else {
                                        gpc.getVbGamePane().getStyleClass().add("gameitemfirst");
                                        gpc.getVbGamePane().applyCss();
                                        second = true;
                                    }
                                }
                                vbPlayerTurnBox.getChildren().add(gpc.getVbGamePane());

                            }
                            if (wdt == null) {
                                startListeningForChanges();
                            }
                        }

                    });
                }

            }).start();
            ;
        }
        timeLeft = 60;
        if (systemTray != null) {
            systemTray.setStatus(playerGames.size() + " games await your turn.");
        }
        // currentGames.notify();

    }

    private void updateLabel() {

        timeLeft--;
        if (JGMRConfig.getInstance().getPlayerSteamId() != null) {
            lbTime.setText("Time left until next pull: " + timeLeft + " seconds");
        } else {
            lbTime.setText("Please enter your authcode in the settings..." + " Next pull: " + timeLeft + " seconds");
        }

    }

    @FXML
    private void uploadGameManually() {
        Stage dialog = new Stage();
        Scene scene = getScene("uploadSaveGameDialog.fxml");
        UploadSaveFileDialogController usfd = (UploadSaveFileDialogController) scene.getUserData();
        usfd.setGames(playerGames);
        dialog.setTitle("Giant Multi Robot Java-Client Save uploader");
        dialog.setScene(scene);
        dialog.show();
    }

    @FXML
    private void settingsButton() {
        Stage dialog = new Stage();
        Scene scene = getScene("settingsDialog.fxml");
        dialog.setTitle("Giant Multi Robot Java-Client");
        dialog.setScene(scene);
        dialog.show();
    }

    private void initializeListViews() {
        lvAllGames.setCellFactory(new Callback<ListView<Game>, ListCell<Game>>() {
            @Override
            public ListCell<Game> call(ListView<Game> param) {
                ListCell<Game> cell = new ListCell<Game>() {
                    @Override
                    protected void updateItem(Game g, boolean b) {
                        super.updateItem(g, b);
                        if (g != null) {
                            setText(g.toString());
                        }
                    }
                };
                return cell;
            }

        });
    }

    private void startListeningForChanges() {
        if (JGMRConfig.getInstance().getPath() != null) {
            if (wdt == null) {
                if (playerGames != null) {
                    wd = new WatchDirectory(playerGames) {
                        @Override
                        public void updatedSaveFile(SaveFile file) {
                            handleNewSaveFile(file);
                        }
                    };
                }
                wdt = new Thread(wd);
                wdt.setDaemon(true);
                wdt.start();
            }
        }
    }

    private void pauseWatchService() {
        if (wd != null) {
            wd.setNewDownload();
        }
    }

    private void handleNewSaveFile(SaveFile file) {
        String fileName = file.toString();
        Platform.runLater(() -> {
            if (!newDownload) {
                if (!newSaveFileDialog.isShowing()) {
                    newSaveFileDialog.getItems().removeAll();
                    newSaveFileDialog.getItems().addAll(playerGames);
                    Optional<Game> result = newSaveFileDialog.showAndWait();
                    if (result.isPresent()) {
                        int index = playerGames.indexOf(result);

                        jgmrVbox.getChildren().add(hbUpload);
                        pbUpload.setProgress(-1.0f);
                        Task t = new Task() {
                            @Override
                            protected Object call() throws Exception {
                                if (result.isPresent()) {
                                    boolean didUpload = gc.uploadSaveFile(result.get(), fileName);
                                    if (!didUpload) {

                                        Platform.runLater(() -> {
                                            Dialog dialog = new Dialog();
                                            dialog.setTitle("Couldn't upload savefile");
                                            dialog.setContentText("The savefile didn't succesfully upload to GMR, try again later or upload the savefile through the website");
                                            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

                                            dialog.show();
                                            ((Stage) dialog.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
                                            pbUpload.setProgress(0);
                                            jgmrVbox.getChildren().remove(hbUpload);
                                        });

                                    } else {
                                        Platform.runLater(() -> {
                                            TrayNotification uploadSucces = new TrayNotification("Upload successful", "", Notifications.SUCCESS);
                                            uploadSucces.setAnimation(Animations.POPUP);
                                            uploadSucces.showAndDismiss(Duration.seconds(3));
                                            pbUpload.setProgress(0);
                                            jgmrVbox.getChildren().remove(hbUpload);
                                        });

                                    }
                                }
                                return null;
                            }
                        };
                        Thread thread = new Thread(t);
                        thread.start();

                    }
                }
            }
            JGMRConfig.getInstance().readDirectory();
        });
    }

    private void resumeWatchService() {
        if (wd != null) {
            wd.activateWatchService();
        }
    }

    private void initializeWatcher() throws IOException {

    }

    private void displayNotification() {
        Image gmrLogo = new Image(getClass().getResourceAsStream("GMRLogo.png"));
        notification = new TrayNotification("It's your turn", "It's your turn in " + playerGames.size() + " games", Notifications.SUCCESS);
        notification.setRectangleFill(Paint.valueOf("#565656"));
        notification.setImage(gmrLogo);
        notification.setAnimation(Animations.POPUP);
        notification.showAndDismiss(Duration.seconds(15));
    }

    private void initializeNotifications() {
        if (lvAllGames.getScene() != null) {
            Stage stage = (Stage) lvAllGames.getScene().getWindow();
            if (playerGames.size() > 0 && JGMRConfig.getInstance().getNotificationFrequency() > 0) {
                if (JGMRConfig.getInstance().isNotificationsMinized() && stage.isIconified()) {
                    displayNotification();
                } else if (JGMRConfig.getInstance().isNotificationsMinized() && !stage.isShowing()) {
                    displayNotification();
                } else if (!stage.isIconified() && stage.isShowing()) {
                    displayNotification();
                }
            }
        }
        if (JGMRConfig.getInstance().getNotificationFrequency() > 0) {
            notificationTimeline = new Timeline(new KeyFrame(
                    Duration.minutes(JGMRConfig.getInstance().getNotificationFrequency()),
                    ae -> initializeNotifications()));
            notificationTimeline.play();
        } else {
            notificationTimeline = new Timeline(new KeyFrame(
                    Duration.minutes(5),
                    ae -> initializeNotifications()));
            notificationTimeline.play();
        }
    }

    private void updateDownloadProgressBar(double size) {
        Platform.runLater(() -> {
            pbDownload.setProgress(size);
        });
    }

    private void initializeSystemtray() {
        //SystemTray.SWING_UI = new CustomSwingUI();
        Stage stage = (Stage) lvAllGames.getScene().getWindow();

        systemTray = SystemTray.get();
        if (systemTray != null) {

            systemTray.setImage(getClass().getResource("eicon.png"));
            if (playerGames.size() > 0) {
                systemTray.setStatus(playerGames.size() + " games await your turn.");
            }
            //OPEN
            systemTray.getMenu().add(new dorkbox.systemTray.MenuItem("Show", new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //Platform.runlater is needed otherwise the stage will not load anymmore
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            stage.show();
                        }
                    });
                }
            }));
            //QUIT
            systemTray.getMenu().add(new dorkbox.systemTray.MenuItem("Quit JavaGMR", new ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //Platform.runlater is needed otherwise the stage will not load anymmore
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Platform.exit();
                        }
                    });
                }
            }));

        }
    }

    private void checkForUpdates() {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("version.properties"));
        } catch (IOException ex) {
            Logger.getLogger(UpdateDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        GithubUtility gu = new GithubUtility("eternia16", "javaGMR", props.getProperty("version"));
        Thread t = new Thread(() -> {
            try {
                if (gu.checkForUpdates()) {
                    Release release = gu.getLatestRelease();
                    Platform.runLater(() -> {
                        Scene scene = getScene("updateDialog.fxml");
                        UpdateDialogController udc = (UpdateDialogController) scene.getUserData();
                        udc.setAssets(release.getAssets());
                        Stage dialog = new Stage();
                        dialog.setTitle("Update detected");
                        dialog.setScene(scene);

                        dialog.show();
                    });
                }
            } catch (UnirestException ex) {
                Logger.getLogger(JgmrGuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.run();
    }
}
