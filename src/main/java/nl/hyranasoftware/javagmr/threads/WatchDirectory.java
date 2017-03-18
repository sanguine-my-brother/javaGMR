/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.threads;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import nl.hyranasoftware.javagmr.util.JGMRConfig;

/**
 *
 * @author danny_000
 */
public class WatchDirectory implements Runnable {

    WatchService watcher;
    Dialog newSaveFile;
    
    



    public void processEvents() throws Exception {
        if (JGMRConfig.getInstance().getPath() != null) {
            watcher = FileSystems.getDefault().newWatchService();
            Path dir = new File(JGMRConfig.getInstance().getPath()).toPath();
            try {
                WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);

            } catch (IOException x) {
                System.err.println(x);
            }
            for (;;) {

                WatchKey key;

                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted Exception");
                    return;
                }

                List<WatchEvent<?>> eventList = key.pollEvents();

                for (WatchEvent<?> genericEvent : eventList) {

                    WatchEvent.Kind<?> eventKind = genericEvent.kind();
                    System.out.println("Event kind: " + eventKind);

                    if (eventKind == OVERFLOW) {

                        continue; // pending events for loop
                    }

                    WatchEvent pathEvent = (WatchEvent) genericEvent;
                    Path file = (Path) pathEvent.context();
                                Platform.runLater(() -> {
                                   newSaveFile = new Dialog();
                    newSaveFile.show();
            });

                    System.out.println("New save file detected: " + file.toString());

                }

                boolean validKey = key.reset();
                System.out.println("Key reset");
                System.out.println("");

                if (!validKey) {
                    System.out.println("Invalid key");
                    break; // infinite for loop
                }

            } // end infinite for loop
        }
        return;
        
        
    }

    @Override
    public void run() {
        try {
            Platform.runLater(() -> {
                // code that updates UI
            });
            processEvents();
        } catch (Exception ex) {
            Logger.getLogger(WatchDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
