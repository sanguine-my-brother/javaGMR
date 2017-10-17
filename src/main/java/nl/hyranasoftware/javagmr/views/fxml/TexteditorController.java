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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import nl.hyranasoftware.javagmr.domain.Note;

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

    public void constructView(Note note){
        this.note = note;
        taNoteText.setText(note.getText());
        
    }
    
    @FXML
    public void saveNotes(){
        save();
    }
    
    public void saveOnExit(){
        save();
    }
    
    private void save(){
        try {
            this.note.setText(taNoteText.getText());
            ObjectMapper mapper = new ObjectMapper();
            File noteFile = new File("notes/" + this.note.getGameid() + ".json");
            if(!noteFile.exists()){
                noteFile.getParentFile().mkdirs();
                noteFile.createNewFile();
            }
            mapper.writeValue(noteFile, note);
        } catch (IOException ex) {
            Logger.getLogger(TexteditorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
