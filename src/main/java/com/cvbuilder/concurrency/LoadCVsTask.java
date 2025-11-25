package com.cvbuilder.concurrency;

import com.cvbuilder.database.CVData;
import com.cvbuilder.database.DatabaseManager;

import java.util.List;

/**
 * Background task for loading CVs from database
 */
public class LoadCVsTask extends CVTask<List<CVData>> {

    private final DatabaseManager dbManager;

    public LoadCVsTask(DatabaseManager dbManager) {
        super("LoadCVs");
        this.dbManager = dbManager;
    }

    @Override
    protected List<CVData> call() throws Exception {
        updateMessage("Loading CVs from database...");
        updateProgress(-1, 100); // Indeterminate progress

        // Simulate some processing time
        Thread.sleep(300);

        List<CVData> cvList = dbManager.getAllCVs();

        updateMessage("Loaded " + cvList.size() + " CVs");
        return cvList;
    }
}