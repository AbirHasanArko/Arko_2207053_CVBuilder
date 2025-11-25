package com.cvbuilder.concurrency;

import javafx.application.Platform;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages automatic saving of CV data in the background
 */
public class AutoSaveManager {

    private ScheduledFuture<?> autoSaveTask;
    private final CVTaskExecutor executor;
    private Runnable saveAction;
    private boolean isEnabled = false;

    public AutoSaveManager() {
        this.executor = CVTaskExecutor.getInstance();
    }

    /**
     * Start auto-save with specified interval
     */
    public void startAutoSave(Runnable saveAction, int intervalSeconds) {
        if (isEnabled) {
            stopAutoSave();
        }

        this.saveAction = saveAction;
        this.isEnabled = true;

        autoSaveTask = executor.scheduleAtFixedRate(() -> {
            System.out.println("Auto-save triggered...");
            Platform.runLater(saveAction); // Run on JavaFX thread
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);

        System.out.println("Auto-save enabled (interval: " + intervalSeconds + " seconds)");
    }

    /**
     * Stop auto-save
     */
    public void stopAutoSave() {
        if (autoSaveTask != null && !autoSaveTask.isCancelled()) {
            autoSaveTask.cancel(false);
            System.out.println("Auto-save disabled");
        }
        isEnabled = false;
    }

    /**
     * Check if auto-save is enabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Manually trigger save immediately
     */
    public void saveNow() {
        if (saveAction != null) {
            executor.scheduleOnce(() -> {
                Platform.runLater(saveAction);
            }, 0, TimeUnit.SECONDS);
        }
    }
}