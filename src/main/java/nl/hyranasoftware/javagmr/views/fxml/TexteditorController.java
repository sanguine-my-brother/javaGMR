/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.views.fxml;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import nl.hyranasoftware.javagmr.domain.Note;
import nl.hyranasoftware.javagmr.util.JGMRConfig;

/**
 * FXML Controller class
 *
 * @author danny_000
 */
public class TexteditorController implements Initializable {

    @FXML
    private TextArea taNoteText;
    @FXML
    private Button btSave;

    private Note note;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btSave.getStyleClass().add("success");
        btSave.applyCss();
    }

    public void constructView(Note note) {
        this.note = note;
        taNoteText.setText(note.getText());

    }

    @FXML
    public void saveNotes() {
        save();
    }

    public void saveOnExit() {
        if (!taNoteText.getText().equals(this.note.getText())) {
            if (!JGMRConfig.getInstance().isDontAskMeToSave()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Save notes?");
                alert.setHeaderText("Would you like to save your notes?");
                ButtonType save = new ButtonType("Save");
                ButtonType dontaskmeagain = new ButtonType("Don't ask me again");
                ButtonType dontsave = new ButtonType("Don't save", ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(save, dontaskmeagain, dontsave);
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == save){
                    save();
                }else if(result.get() == dontaskmeagain){
                    JGMRConfig.getInstance().setDontAskMeToSave(true);
                    save();
                }
                
            } else {
                save();
            }
        }
    }

    private void save() {
        try {
            this.note.setText(taNoteText.getText());
            ObjectMapper mapper = new ObjectMapper();
            File noteFile = new File("notes/" + this.note.getGameid() + ".json");
            if (!noteFile.exists()) {
                noteFile.getParentFile().mkdirs();
                noteFile.createNewFile();
            }
            mapper.writeValue(noteFile, note);
        } catch (IOException ex) {
            Logger.getLogger(TexteditorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
