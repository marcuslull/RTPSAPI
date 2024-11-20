package com.marcuslull.rtpsapi.listeners.bluesky;

import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * Service for managing a Bluesky firehose process, which continuously collects data from the Bluesky social media platform.
 * This service uses a {@link ScheduledExecutorService} to periodically start a Node.js script that handles the firehose connection.
 */
@Service
public class BlueskyFirehose {

    private final String SCRIPT_COMMAND = "node";
    private final String SCRIPT_LOCATION = "./firehoseClient/firehose.js";
    private ScheduledExecutorService firehoseExecutor;

    /**
     * Starts the Bluesky firehose process.
     * This method creates a {@link ScheduledExecutorService} that periodically executes a Node.js script
     * to connect to the Bluesky firehose and collect data.  The Node.js script's execution time is controlled by `workerLifespanSeconds`.
     * The executor itself has a lifespan determined by `threadPoolLifespanSeconds`.
     *
     * @param threadPoolSize            The number of threads to allocate in the thread pool.  Should be 1 for this use case.
     * @param threadPoolLifespanSeconds The lifespan of the thread pool in seconds. After this time, the executor will shut down.
     * @param workerLifespanSeconds    The lifespan of each individual Node.js firehose worker process in seconds.
     * @param workerStartupDelay       The initial delay before the first firehose worker starts, in seconds.
     * @param workerIntervalSeconds    The time interval between the start of consecutive firehose worker processes, in seconds.
     * @return The {@link ScheduledExecutorService} managing the firehose process.
     */
    public ScheduledExecutorService connect(int threadPoolSize,
                                            int threadPoolLifespanSeconds,
                                            int workerLifespanSeconds,
                                            int workerStartupDelay,
                                            int workerIntervalSeconds) {

        System.out.println("Firehose Executor - Firehose initializing...");

        firehoseExecutor = Executors.newScheduledThreadPool(threadPoolSize);

        Runnable firehoseTask = () -> {
            Process worker;
            try {
                worker = new ProcessBuilder(SCRIPT_COMMAND, SCRIPT_LOCATION)
                        .inheritIO() // redirect all console.*() to System.out.*()
                        .start();

                // blocking waitFor to act as a worker lifespan
                if (!worker.waitFor(workerLifespanSeconds, TimeUnit.SECONDS)) {
                    destroyWorker(worker);
                }
            } catch (Exception e) {
                System.out.println("Firehose Task - Firehose worker error");
            }
        };

        firehoseExecutor.scheduleAtFixedRate(firehoseTask, workerStartupDelay, workerIntervalSeconds, TimeUnit.SECONDS);

        firehoseExecutor.schedule(() -> {
            disconnect(firehoseExecutor);
        }, threadPoolLifespanSeconds, TimeUnit.SECONDS);

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

