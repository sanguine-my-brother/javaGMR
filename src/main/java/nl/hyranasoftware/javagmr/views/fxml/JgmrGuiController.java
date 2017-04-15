/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import dorkbox.systemTray.SystemTray;
import dorkbox.systemTray.Checkbox;
import dorkbox.systemTray.Menu;
import dorkbox.systemTray.Separator;
import dorkbox.systemTray.SystemTray;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
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
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.gui;
import nl.hyranasoftware.javagmr.threads.WatchDirectory;
import nl.hyranasoftware.javagmr.util.JGMRConfig;
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
    private HBox hbUpload;
    @FXML
    private HBox hbDownload;
    @FXML
    private ListView lvPlayerTurnGames;
    @FXML
    private ListView lvAllGames;
    @FXML
    private AnchorPane apAbove;
    @FXML
    private Label lbTime;
    @FXML
    private ProgressBar pbDownload;

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

        initializeChoiceDialog();
        initializeContextMenu();
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
                            lvPlayerTurnGames.setItems(playerGames);
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
        FXMLLoader loader = null;
        String url = null;
        url = getClass().getResource("uploadSaveGameDialog.fxml").toString();
        System.out.println("  * url: " + url);
        loader = new FXMLLoader(getClass().getResource("uploadSaveGameDialog.fxml"));
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage dialog = new Stage();
        Scene scene = new Scene(root);
        UploadSaveFileDialogController usfd = loader.<UploadSaveFileDialogController>getController();
        usfd.setGames(playerGames);
        dialog.setTitle("Giant Multi Robot Java-Client Save uploader");
        dialog.setScene(scene);
        dialog.show();
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
                            setText(g.toString());
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
                            setText(g.toString());
                        } else {
                            setText(null);
                        }

                    }
                };
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (cell.getText() != null) {
                            if (event.getButton() == MouseButton.PRIMARY) {
                                cm.show(cell, event.getScreenX(), event.getScreenY());
                            }
                        }
                    }

                });
                cell.setContextMenu(cm);
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
                    Optional<Game> result = newSaveFileDialog.showAndWait();
                    if (result.isPresent()) {
                        boolean didUpload = gc.uploadSaveFile(result.get(), fileName);
                        if (!didUpload) {
                            Dialog dialog = new Dialog();
                            dialog.setTitle("Couldn't upload savefile");
                            dialog.setContentText("The savefile didn't succesfully upload to GMR, try again later or upload the savefile through the website");
                            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                            dialog.show();
                        } else {
                            lvPlayerTurnGames.getItems().remove(result);
                        }
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

    private Dialog initializeDownloadDialog() {
        Dialog dialog = new Dialog();
        dialog.setContentText("The save file has succesfully been downloaded");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.setTitle("Onward my noble Leader and conquer thy enemies");
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, (event) -> {
            resumeWatchService();
        });

        return dialog;
    }

    private void initializeContextMenu() {
        MenuItem downloadSaveFile = new MenuItem("Download Save File");
        downloadSaveFile.setOnAction(event -> {
            pauseWatchService();
            jgmrVbox.getChildren().add(hbDownload);
            newSaveFileDialog.setSelectedItem((Game) lvPlayerTurnGames.getSelectionModel().getSelectedItem());
            Task t = new Task() {
                @Override
                protected Object call() throws Exception {
                                gc.downloadSaveFile((Game) lvPlayerTurnGames.getSelectionModel().getSelectedItem());
                Platform.runLater(() -> {
                    Dialog dialog = new Dialog();
                    dialog.setContentText("The save file has succesfully been downloaded");
                    ((Stage) dialog.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    dialog.setTitle("Onward my noble Leader and conquer thy enemies");
                    dialog.show();
                    pbDownload.setProgress(0);
                    jgmrVbox.getChildren().remove(hbDownload);
                    startListeningForChanges();
                });
                return null;
                }
            };
            Thread thread = new Thread(t);
            thread.start();
        });

        MenuItem goToGameSite = new MenuItem("View game's page on GMR");
        goToGameSite.setOnAction(event -> {
            String webURI = "http://multiplayerrobot.com/Game#" + ((Game) lvPlayerTurnGames.getSelectionModel().getSelectedItem()).getGameid();
            try {
                java.awt.Desktop.getDesktop().browse(URI.create(webURI));
            } catch (IOException ex) {
                Logger.getLogger(JgmrGuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        cm.getItems().addAll(downloadSaveFile, goToGameSite);

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
            boolean test = stage.isShowing();
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
        System.out.println(size);
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
}
