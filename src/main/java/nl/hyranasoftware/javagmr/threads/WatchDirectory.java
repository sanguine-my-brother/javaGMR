/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.threads;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.barbarysoftware.watchservice.*;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_CREATE;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_MODIFY;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.OVERFLOW;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import nl.hyranasoftware.javagmr.controller.GameController;
import nl.hyranasoftware.javagmr.domain.Game;
import nl.hyranasoftware.javagmr.util.GMRLogger;
import nl.hyranasoftware.javagmr.util.JGMRConfig;
import nl.hyranasoftware.javagmr.util.SaveFile;

/**
 *
 * @author danny_000
 */
public abstract class WatchDirectory implements Runnable {

    String fileName;
    long previousFileSize = 0;
    private volatile boolean newDownload = false;

    int index = 0;
    GameController gc = new GameController();

    public WatchDirectory(List<Game> playerGames) {

    }

    public void setNewDownload() {
        newDownload = true;
    }

    /**
     * This method is a placeholder until I can figure out a better way to
     * combine the barbary watchservice and the Java watchservice in one class.
     * I do NOT like to have duplicate code in one class, however since barbary
     * watchservice is quite slow compared to the normal watchservice. I was
     * forced to do so. If someone has a better solution please do share
     *
     * @throws Exception
     */
    public void processEventsMac() throws IOException {
        if (JGMRConfig.getInstance().getPath() != null) {
            com.barbarysoftware.watchservice.WatchService watcher = com.barbarysoftware.watchservice.WatchService.newWatchService();
            WatchableFile hotseatDir = new WatchableFile(new File(JGMRConfig.getInstance().getPath()));
            hotseatDir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
            for (;;) {

                WatchKey key;

                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }

                List<WatchEvent<?>> eventList = key.pollEvents();

                for (WatchEvent<?> genericEvent : eventList) {

                    WatchEvent.Kind<?> eventKind = genericEvent.kind();

                    if (eventKind == OVERFLOW) {

                        continue; // pending events for loop
                    }

                    WatchEvent pathEvent = (WatchEvent) genericEvent;
                    File file = new File(pathEvent.context().toString());
                    GMRLogger.logLine("File path: " + file.getAbsolutePath());
                    if (!newDownload) {
                        GMRLogger.logLine("Event kind: " + eventKind);
                        if (eventKind == ENTRY_CREATE) {
                            updatedSaveFile(new SaveFile(file.getName()));
                            GMRLogger.logLine("New save file detected: " + file.toString());
                        }
                        if (eventKind == ENTRY_MODIFY) {
                            SaveFile saveFile = new SaveFile(file.getAbsolutePath());
                            if (saveFile.getSize() > previousFileSize && previousFileSize != 0) {
                                if (JGMRConfig.getInstance().didSaveFileChange(saveFile)) {
                                    updatedSaveFile(new SaveFile(file.getName()));
                                    GMRLogger.logLine("New save file detected: " + file.toString());
                                }
                            }
                            previousFileSize = saveFile.getSize();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(WatchDirectory.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    }

                }

                boolean validKey = key.reset();

                if (!validKey) {
                    GMRLogger.logLine("Invalid key");
                    break; // infinite for loop
                }

            } // end infinite for loop
        }

    }

    /**
     * This method is a placeholder until I can figure out a better way to
     * combine the barbary watchservice and the Java watchservice in one class.
     * I do NOT like to have duplicate code in one class, however since barbary
     * watchservice is quite slow compared to the normal watchservice. I was
     * forced to do so. If someone has a better solution please do share
     *
     * @throws Exception
     */
    public void processEventsWinLin() {
        if (JGMRConfig.getInstance().getPath() != null) {
            java.nio.file.WatchService watcher = null;
            try {
                watcher = FileSystems.getDefault().newWatchService();
            } catch (IOException ex) {
                Logger.getLogger(WatchDirectory.class.getName()).log(Level.SEVERE, null, ex);
            }
            Path dir = new File(JGMRConfig.getInstance().getPath()).toPath();
            try {
                dir.register(watcher, java.nio.file.StandardWatchEventKinds.ENTRY_CREATE, java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);

            } catch (IOException x) {
                System.err.println(x);
            }
            for (;;) {

                java.nio.file.WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    // clean up state...
                    Thread.currentThread().interrupt();
                    break;
                }

                List<java.nio.file.WatchEvent<?>> eventList = key.pollEvents();

                for (java.nio.file.WatchEvent<?> genericEvent : eventList) {

                    java.nio.file.WatchEvent.Kind<?> eventKind = genericEvent.kind();

                    if (eventKind == java.nio.file.StandardWatchEventKinds.OVERFLOW) {

                        continue; // pending events for loop
                    }

                    java.nio.file.WatchEvent pathEvent = (java.nio.file.WatchEvent) genericEvent;
                    Path file = (Path) pathEvent.context();
                    if (!newDownload) {
                        GMRLogger.logLine("Event kind: " + eventKind);
                        if (eventKind == java.nio.file.StandardWatchEventKinds.ENTRY_CREATE) {
                            updatedSaveFile((SaveFile) file.toFile());
                            GMRLogger.logLine("New save file detected: " + file.toString());
                        }
                        if (eventKind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY) {
                            SaveFile saveFile = new SaveFile(JGMRConfig.getInstance().getPath() + "/" + file.toString());
                            if (JGMRConfig.getInstance().didSaveFileChange(saveFile)) {
                                updatedSaveFile(new SaveFile(file.getFileName().toString()));
                                GMRLogger.logLine("New save file detected: " + file.toString());
                            }

                        }
                    }

                }

                boolean validKey = key.reset();

                if (!validKey) {
                    GMRLogger.logLine("Invalid key");
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
            String osName = System.getProperty("os.name").toLowerCase();
            if (!osName.contains("mac")) {
                processEventsMac();
            } else {
                processEventsWinLin();
            }

        } catch (Exception ex) {
            Logger.getLogger(WatchDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void activateWatchService() {
        this.newDownload = false;
    }

}
