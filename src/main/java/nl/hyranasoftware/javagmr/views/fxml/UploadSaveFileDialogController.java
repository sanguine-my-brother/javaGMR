/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.util.JGMRConfig;

/**
 *
 * @author danny_000
 */
public class UploadSaveFileDialogController implements Initializable {

    @FXML
    ComboBox lvGames;

    @FXML
    TextField tbSaveFile;

    GameController gc = new GameController();
    ObservableList<Game> playerTurns = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvGames.setItems(playerTurns);
    }

    public void setGames(ObservableList<Game> playerTurns) {
        this.playerTurns = playerTurns;
        lvGames.setItems(playerTurns);
    }

    @FXML
    protected void cancel() {
        Stage stage = (Stage) lvGames.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void uploadSaveGame() {
        System.out.println(lvGames.getSelectionModel().getSelectedIndex());
        File file = new File(tbSaveFile.getText());
        if (lvGames.getSelectionModel().getSelectedIndex() > -1 && file.exists()) {
            boolean uploadStatusSucces = gc.uploadSaveFile((Game) lvGames.getSelectionModel().getSelectedItem(), file.getName());
            if (uploadStatusSucces) {
                Stage stage = (Stage) lvGames.getScene().getWindow();
                stage.close();
            } else {
                Dialog dg = new Dialog();
                dg.setContentText("Either check on GMR if it's your turn or try uploading it again");
                dg.getDialogPane().getButtonTypes().add(ButtonType.OK);
                dg.setTitle("Could not upload savefile");
                dg.show();
            }
        } else {
            Dialog dg = new Dialog();
            dg.setContentText("Either you haven't selected a game or the file path is incorrect");
            dg.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dg.setTitle("Incorrect upload");
            dg.show();
        }

    }

    @FXML
    protected void refreshGames() {
        setGames(FXCollections.observableArrayList(gc.retrievePlayersTurns(gc.getGames())));
    }

    @FXML
    protected void browseToSaveFile() {
        FileChooser directoryChooser = new FileChooser();
        if (JGMRConfig.getInstance().getPath() != null) {
            directoryChooser.setInitialDirectory(new File(JGMRConfig.getInstance().getPath()));
        }
        Stage stage = new Stage();
        File fileResult = directoryChooser.showOpenDialog(stage);
        if (fileResult != null) {
            tbSaveFile.setText(fileResult.getAbsolutePath());
        }

    }

}
