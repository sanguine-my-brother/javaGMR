/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
public class JgmrGuiController implements Initializable {

    @FXML
    private Button btSettings;

    @FXML
    private ListView lvPlayerTurnGames;
    @FXML
    private ListView lvAllGames;
    @FXML
    private AnchorPane apAbove;
    @FXML
    private Label lbTime;

    ContextMenu cm = new ContextMenu();
    boolean newDownload;
    WatchDirectory wd;
    Thread wdt;
    ObservableList<Game> currentGames = FXCollections.observableArrayList();
    ObservableList<Game> playerGames = FXCollections.observableArrayList();
    ChoiceDialog<Game> newSaveFileDialog;
    int timeLeft;

    GameController gc = new GameController();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeChoiceDialog();
        initializeContextMenu();
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

    }

    private void initializeChoiceDialog() {
        newSaveFileDialog = new ChoiceDialog<>(null, playerGames);
        newSaveFileDialog.setTitle("Save file");
        newSaveFileDialog.setHeaderText("I see you played your turn");
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
                        } else{
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
            newSaveFileDialog.setSelectedItem((Game) lvPlayerTurnGames.getSelectionModel().getSelectedItem());
            gc.downloadSaveFile((Game) lvPlayerTurnGames.getSelectionModel().getSelectedItem());
            Dialog dialog = initializeDownloadDialog();
            dialog.show();
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
}
