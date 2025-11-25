package com.cvbuilder.concurrency;

import javafx.concurrent.Task;

/**
 * Base class for CV-related background tasks
 */
public abstract class CVTask<T> extends Task<T> {
    
    protected String taskName;
    
    public CVTask(String taskName) {
        this.taskName = taskName;
    }
    
    @Override
    protected void scheduled() {
        System.out.println("[" + taskName + "] Task scheduled");
    }
    
    @Override
    protected void running() {
        System.out.println("[" + taskName + "] Task running...");
    }
    
    @Override
    protected void succeeded() {
        System.out.println("[" + taskName + "] Task completed successfully");
    }
    
    @Override
    protected void failed() {
        System.err.println("[" + taskName + "] Task failed: " + getException());
    }
}