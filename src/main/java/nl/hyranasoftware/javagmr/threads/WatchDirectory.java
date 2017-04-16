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
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.util.JGMRConfig;
import nl.hyranasoftware.javagmr.util.SaveFile;

/**
 *
 * @author danny_000
 */
public abstract class WatchDirectory implements Runnable {

    WatchService watcher;
    String fileName;
    private volatile boolean newDownload = false;

    int index = 0;
    GameController gc = new GameController();

    public WatchDirectory(List<Game> playerGames) {

    }

    public void setNewDownload() {
        newDownload = true;
    }

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
                    break;
                }

                List<WatchEvent<?>> eventList = key.pollEvents();

                for (WatchEvent<?> genericEvent : eventList) {

                    WatchEvent.Kind<?> eventKind = genericEvent.kind();

                    if (eventKind == OVERFLOW) {

                        continue; // pending events for loop
                    }

                    WatchEvent pathEvent = (WatchEvent) genericEvent;
                    Path file = (Path) pathEvent.context();
                    if (!newDownload) {
                        System.out.println("Event kind: " + eventKind);
                        if (eventKind == ENTRY_CREATE) {
                            updatedSaveFile((SaveFile) file.toFile());
                            System.out.println("New save file detected: " + file.toString());
                        }
                        if (eventKind == ENTRY_MODIFY) {
                            SaveFile saveFile = new SaveFile(JGMRConfig.getInstance().getPath() + "/" + file.toString());
                            if (JGMRConfig.getInstance().didSaveFileChange(saveFile)) {
                                updatedSaveFile(new SaveFile(file.getFileName().toString()));
                                System.out.println("New save file detected: " + file.toString());
                            }

                        }
                    }

                }

                boolean validKey = key.reset();

                if (!validKey) {
                    System.out.println("Invalid key");
                    break; // infinite for loop
                }

            } // end infinite for loop
        }

    }

    private void launchDialog(Path file) {

    }

    public abstract void updatedSaveFile(SaveFile file);

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void run() {
        try {
            processEvents();

        } catch (Exception ex) {
            Logger.getLogger(WatchDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void activateWatchService() {
        this.newDownload = false;
    }

}
