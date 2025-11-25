package com.cvbuilder.concurrency;

import com.cvbuilder.database.CVData;
import com.cvbuilder.database.DatabaseManager;

/**
 * Background task for saving CV to database
 */
public class SaveCVTask extends CVTask<Integer> {

    private final CVData cvData;
    private final DatabaseManager dbManager;
    private final Integer cvId; // null for new, ID for update

    public SaveCVTask(CVData cvData, Integer cvId, DatabaseManager dbManager) {
        super("SaveCV");
        this.cvData = cvData;
        this.cvId = cvId;
        this.dbManager = dbManager;
    }

    @Override
    protected Integer call() throws Exception {
        updateMessage("Saving CV to database...");
        updateProgress(0, 100);

        // Simulate some processing time
        Thread.sleep(500);
        updateProgress(50, 100);

        int result;
        if (cvId != null) {
            // Update existing CV
            cvData.setId(cvId);
            boolean success = dbManager.updateCV(cvData);
            result = success ? cvId : -1;
        } else {
            // Add new CV
            result = dbManager.addCV(cvData);
        }

        updateProgress(100, 100);
        updateMessage("CV saved successfully!");

        return result;
    }
}