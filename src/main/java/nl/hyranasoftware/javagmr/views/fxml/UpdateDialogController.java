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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import nl.hyranasoftware.githubupdater.GithubUtility;
import nl.hyranasoftware.githubupdater.domain.Asset;
import nl.hyranasoftware.javagmr.util.JGMRConfig;
import nl.hyranasoftware.javagmr.util.OpenURL;

/**
 * FXML Controller class
 *
 * @author danny_000
 */
public class UpdateDialogController implements Initializable {

    GithubUtility githubUtility;

    @FXML
    private Label lbDownload;
    @FXML
    private Hyperlink urlReleases;
    @FXML
    private ComboBox<Asset> cbAssets;
    @FXML
    private TextField tbPath;
    @FXML
    private Button tbBrowse;
    @FXML
    private Button btDownload;
    @FXML
    private Button btCancel;
    @FXML
    private ProgressBar pbDownload;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("version.properties"));
        } catch (IOException ex) {
            Logger.getLogger(UpdateDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        githubUtility = new GithubUtility("eternia16", "javaGMR", props.getProperty("version")) {
            @Override
            public void sendDownloadProgress(double percent) {
                updateDownloadProgressBar(percent);
            }
        };
    }

    public void setAssets(List<Asset> assets) {
        cbAssets.getItems().addAll(assets);
        cbAssets.getSelectionModel().select(0);
    }

    @FXML
    protected void openReleases() {
        OpenURL.openUrlInBrowser("https://github.com/eternia16/javaGMR/releases");
    }

    @FXML
    protected void selectDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (JGMRConfig.getInstance().getPath() != null) {
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        Stage stage = new Stage();
        File fileResult = directoryChooser.showDialog(stage);
        if (fileResult != null) {
            tbPath.setText(fileResult.getAbsolutePath());
        }

    }

    @FXML
    protected void closeUpdateDialog() {
        Stage stage = (Stage) tbBrowse.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void downloadAsset() {
        if (cbAssets.getSelectionModel().getSelectedIndex() == -1) {
            Dialog dialog = new Dialog();
            dialog.setTitle("Error");
            dialog.setContentText("Please select the file you would like to download");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            return;
        }
        if (tbPath.getText().equals("")) {
            Dialog dialog = new Dialog();
            dialog.setTitle("Error");
            dialog.setContentText("Please select a path to download the file to");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            return;
        }
        Path path = Paths.get(tbPath.getText());
        Asset asset = cbAssets.getSelectionModel().getSelectedItem();
        Thread t = new Thread(() -> {
            try {

                githubUtility.downloadAssetToSpecificLocation(path, asset);
                Platform.runLater(() -> {
                    pbDownload.setVisible(false);
                    lbDownload.setVisible(false);
                    TrayNotification downloadSucces = new TrayNotification("Download successful", "", Notifications.SUCCESS);
                    downloadSucces.setMessage("Please close this client and open the new one");
                    downloadSucces.setAnimation(Animations.POPUP);
                    downloadSucces.showAndDismiss(Duration.seconds(6));
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Dialog dialog = new Dialog();
                    dialog.setTitle("Error");
                    dialog.setContentText("Something went wrong with downloading the file");
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                });
            }
        });
        t.setName("DownloadingUpdate");
        pbDownload.setVisible(true);
        lbDownload.setVisible(true);
        t.start();

    }

    private void updateDownloadProgressBar(double size) {
        Platform.runLater(() -> {
            pbDownload.setProgress(size);
        });
    }
}
