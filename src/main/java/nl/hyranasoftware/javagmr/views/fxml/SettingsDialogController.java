/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import nl.hyranasoftware.javagmr.exception.InValidUserException;
import nl.hyranasoftware.javagmr.util.JGMRConfig;

/**
 * FXML Controller class
 *
 * @author danny_000
 */
public class SettingsDialogController implements Initializable {

    @FXML
    TextField tbSaveDirectory;
    @FXML
    TextField tbAuthCode;
    @FXML
    ComboBox cbFrequency;
    @FXML
    CheckBox cbMinimized;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (JGMRConfig.getInstance().getPath() != null) {
            tbSaveDirectory.setText(JGMRConfig.getInstance().getPath());
        }
        if (JGMRConfig.getInstance().getAuthCode() != null) {
            tbAuthCode.setText(JGMRConfig.getInstance().getAuthCode());
        }
        cbFrequency.getItems().addAll("15 minutes", "30 minutes", "60 minutes", "never");
        switch (JGMRConfig.getInstance().getNotificationFrequency()) {
            case 0:
                cbFrequency.getSelectionModel().select("never");
                break;
            case 15:
                cbFrequency.getSelectionModel().select("15 minutes");
                break;
            case 30:
                cbFrequency.getSelectionModel().select("30 minutes");
                break;
            case 60:
                cbFrequency.getSelectionModel().select("60 minutes");
                break;
        }
        cbMinimized.selectedProperty().set(JGMRConfig.getInstance().isNotificationsMinized());
    }

    @FXML
    private void browseSaveDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Stage stage = new Stage();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            selectedDirectory.getAbsolutePath();
        }
        tbSaveDirectory.setText(selectedDirectory.getAbsolutePath());
    }

    @FXML
    private void saveConfig() {
        try {
            JGMRConfig.getInstance().setAuthCode(tbAuthCode.getText());
            JGMRConfig.getInstance().setPath(tbSaveDirectory.getText());
            JGMRConfig.getInstance().setNotificationsMinized(cbMinimized.selectedProperty().get());
            String frequency = (String) cbFrequency.getSelectionModel().getSelectedItem();
            switch (frequency) {
                case "15 minutes":
                    JGMRConfig.getInstance().setNotificationFrequency(15);
                    break;
                case "30 minutes":
                    JGMRConfig.getInstance().setNotificationFrequency(30);
                    break;
                case "60 minutes":
                    JGMRConfig.getInstance().setNotificationFrequency(60);
                    break;
                case "never":
                    JGMRConfig.getInstance().setNotificationFrequency(0);
                    break;
            }
            Stage stage = (Stage) tbAuthCode.getScene().getWindow();
            stage.close();
        } catch (InValidUserException ex) {
            Dialog dg = new Dialog();
            dg.setContentText("Invalid Authcode");
            dg.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dg.setTitle("Invalid code");
            dg.show();
        }
    }

}
