package com.cvbuilder.concurrency;

import javafx.concurrent.Task;

import java.util.concurrent.*;

/**
 * Manages thread pool for CV Builder operations
 * Singleton pattern for centralized thread management
 */
public class CVTaskExecutor {
    
    private static CVTaskExecutor instance;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;
    
    private CVTaskExecutor() {
        // Create thread pool with 4 threads
        this.executorService = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private int counter = 0;
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("CVBuilder-Worker-" + (++counter));
                thread.setDaemon(true); // Won't prevent JVM shutdown
                return thread;
            }
        });
        
        // Create scheduled executor for periodic tasks
        this.scheduledExecutor = Executors.newScheduledThreadPool(2, new ThreadFactory() {
            private int counter = 0;
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("CVBuilder-Scheduler-" + (++counter));
                thread.setDaemon(true);
                return thread;
            }
        });
        
        System.out.println("CVTaskExecutor initialized with thread pool");
    }
    
    public static synchronized CVTaskExecutor getInstance() {
        if (instance == null) {
            instance = new CVTaskExecutor();
        }
        return instance;
    }
    
    /**
     * Execute a task asynchronously
     */
    public <T> Future<T> executeTask(Callable<T> task) {
        return executorService.submit(task);
    }
    
    /**
     * Execute a JavaFX Task
     */
    public void executeTask(Task<?> task) {
        executorService.submit(task);
    }
    
    /**
     * Schedule a task to run periodically
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, 
                                                   long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }
    
    /**
     * Schedule a task to run once after a delay
     */
    public ScheduledFuture<?> scheduleOnce(Runnable task, long delay, TimeUnit unit) {
        return scheduledExecutor.schedule(task, delay, unit);
    }
    
    /**
     * Shutdown executor gracefully
     */
    public void shutdown() {
        System.out.println("Shutting down CVTaskExecutor...");
        executorService.shutdown();
        scheduledExecutor.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Get executor service for direct access
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }
}