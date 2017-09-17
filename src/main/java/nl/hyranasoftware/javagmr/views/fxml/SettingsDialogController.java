/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
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
    @FXML
    CheckBox cbSystemtray;
    @FXML
    CheckBox cbSaveLogInformation;
    @FXML
    CheckBox cbShowSave;

    private final String never = "Never";
    private final String fifteenmin = "15 minutes";
    private final String thirtymin = "30 mintues";
    private final String sixtymin = "60 minutes";

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
        cbSystemtray.selectedProperty().set(JGMRConfig.getInstance().isMinimizeToTray());
        cbFrequency.getItems().addAll(fifteenmin, thirtymin, sixtymin, never);
        switch (JGMRConfig.getInstance().getNotificationFrequency()) {
            case 0:
                cbFrequency.getSelectionModel().select(never);
                break;
            case 15:
                cbFrequency.getSelectionModel().select(fifteenmin);
                break;
            case 30:
                cbFrequency.getSelectionModel().select(thirtymin);
                break;
            case 60:
                cbFrequency.getSelectionModel().select(sixtymin);
                break;
        }
        cbMinimized.selectedProperty().set(JGMRConfig.getInstance().isNotificationsMinized());
        cbSaveLogInformation.selectedProperty().set(JGMRConfig.getInstance().isLogToFile());
        cbShowSave.selectedProperty().set(JGMRConfig.getInstance().isSaveFileDialog());
    }

    @FXML
    private void browseSaveDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Stage stage = new Stage();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            selectedDirectory.getAbsolutePath();
            tbSaveDirectory.setText(selectedDirectory.getAbsolutePath());
        }
            
        
    }

    @FXML
    private void saveConfig() {
        JGMRConfig.getInstance().setAuthCode(tbAuthCode.getText());
        JGMRConfig.getInstance().setPath(tbSaveDirectory.getText());
        JGMRConfig.getInstance().setSaveFileDialog(cbShowSave.selectedProperty().get());
        JGMRConfig.getInstance().setLogToFile(cbSaveLogInformation.selectedProperty().get());
        JGMRConfig.getInstance().setNotificationsMinized(cbMinimized.selectedProperty().get());
        JGMRConfig.getInstance().setMinimizeToTray(cbSystemtray.selectedProperty().get());
        Platform.setImplicitExit(!cbSystemtray.selectedProperty().get());
        String frequency = (String) cbFrequency.getSelectionModel().getSelectedItem();
        switch (frequency) {
            case fifteenmin:
                JGMRConfig.getInstance().setNotificationFrequency(15);
                break;
            case thirtymin:
                JGMRConfig.getInstance().setNotificationFrequency(30);
                break;
            case sixtymin:
                JGMRConfig.getInstance().setNotificationFrequency(60);
                break;
            case never:
                JGMRConfig.getInstance().setNotificationFrequency(0);
                break;
        }
        Stage stage = (Stage) tbAuthCode.getScene().getWindow();
        stage.close();
    }

}
