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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbSaveDirectory.setText(JGMRConfig.getInstance().getPath());
        tbAuthCode.setText(JGMRConfig.getInstance().getAuthCode());
    }

    @FXML
    private void browseSaveDirectory(){
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
    private void saveConfig(){
        try {
            JGMRConfig.getInstance().setAuthCode(tbAuthCode.getText());
            JGMRConfig.getInstance().setPath(tbSaveDirectory.getText());
            Stage stage = (Stage) tbAuthCode.getScene().getWindow();
            stage.close();
        } catch (InValidUserException ex) {
            Logger.getLogger(SettingsDialogController.class.getName()).log(Level.SEVERE, null, ex);
            Dialog dg = new Dialog();
            dg.setContentText("Invalid Authcode");
            dg.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dg.setTitle("Invalid code");
            dg.show();
        }
    }
    
    
}
