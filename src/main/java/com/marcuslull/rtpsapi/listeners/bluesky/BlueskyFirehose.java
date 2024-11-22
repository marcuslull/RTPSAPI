package com.marcuslull.rtpsapi.listeners.bluesky;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.marcuslull.rtpsapi.listeners.bluesky.BlueskyConstants.*;

/**
 * Service for managing a Bluesky firehose process, which continuously collects data from the Bluesky social media platform.
 * This service uses a {@link ScheduledExecutorService} to periodically start a Node.js script that handles the firehose connection.
 */
@Service
public class BlueskyFirehose {

    private ScheduledExecutorService firehoseExecutor;




    /**
     * Starts the Bluesky firehose process.
     * This method creates a {@link ScheduledExecutorService} that periodically executes a Node.js script
     * to connect to the Bluesky firehose and collect data.  The Node.js script's execution time is controlled by `workerLifespanSeconds`.
     * The executor itself has a lifespan determined by `threadPoolLifespanSeconds`.
     *
     * @return The {@link ScheduledExecutorService} managing the firehose process.
     */
    public ScheduledExecutorService connect() {

        System.out.println("Firehose Executor - Firehose initializing...");

        firehoseExecutor = Executors.newScheduledThreadPool(FIREHOSE_WORKER_THREAD_POOL_SIZE);

        Runnable firehoseTask = () -> {
            Process worker;
            try {
                worker = new ProcessBuilder(FIREHOSE_WORKER_SCRIPT_RUN_COMMAND, FIREHOSE_WORKER_SCRIPT_LOCATION)
                        .inheritIO() // redirect all console.*() to System.out.*()
                        .start();

                // blocking waitFor to act as a worker lifespan
                if (!worker.waitFor(FIREHOSE_WORKER_LIFESPAN_SECONDS, TimeUnit.SECONDS)) {
                    destroyWorker(worker);
                }
            } catch (Exception e) {
                System.out.println("Firehose Task - Firehose worker error");
            }
        };

        firehoseExecutor.scheduleAtFixedRate(firehoseTask, FIREHOSE_WORKER_STARTUP_DELAY, FIREHOSE_WORKER_INTERVAL_SECONDS, TimeUnit.SECONDS);

        firehoseExecutor.schedule(() -> {
            disconnect(firehoseExecutor);
        }, FIREHOSE_THREAD_POOL_LIFESPAN_SECONDS, TimeUnit.SECONDS);

        System.out.println("Firehose Executor - Firehose started");

        return firehoseExecutor;
    }




    /**
     * Stops the Bluesky firehose process by shutting down the {@link ScheduledExecutorService}.
     * This method initiates an orderly shutdown, attempting to terminate the running Node.js process gracefully.
     * If the graceful shutdown fails within a timeout period, a forceful shutdown is performed.
     * @param firehoseExecutor the executor to shut down.
     */
    public void disconnect(ExecutorService firehoseExecutor) {
        if (firehoseExecutor != null && !firehoseExecutor.isShutdown()) {
            firehoseExecutor.shutdownNow();
            System.out.println("Firehose Executor - Firehose stopped");
        }
    }




    /**
     * Destroys a firehose worker process.
     * This method attempts to terminate the specified {@link Process} gracefully.
     * If the process does not exit within a timeout period, it is forcibly destroyed.
     *
     * @param worker The {@link Process} representing the firehose worker to destroy.
     */
    private void destroyWorker(Process worker) {
        if (worker != null) {
            worker.destroy();
            worker.onExit().thenRun(() -> {
                System.out.println("Firehose Task - Worker destroyed");
            });
        }
    }
}

