package com.marcuslull.rtpsapi.listeners;

import com.marcuslull.rtpsapi.listeners.bluesky.model.BlueskyBatchEvent;
import com.marcuslull.rtpsapi.listeners.bluesky.service.BlueskyMessageBatcher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class BlueskyPipeline {

    private final ExecutorService executorService;
    private final BlockingQueue<Runnable> queue;
    private final BlueskyMessageBatcher blueskyMessageBatcher;
    private final ApplicationEventPublisher applicationEventPublisher;

    public BlueskyPipeline(BlueskyMessageBatcher blueskyMessageBatcher, ApplicationEventPublisher applicationEventPublisher) {
        this.blueskyMessageBatcher = blueskyMessageBatcher;
        this.applicationEventPublisher = applicationEventPublisher;
        this.queue = new SynchronousQueue<>();
        this.executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, queue);
    }

    public void start() {
        executorService.execute(worker);
    }

    public void stop() {
        try {
            if (!this.executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            System.out.println("Bluesky pipeline is shutdown");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private final Runnable worker = new Runnable() {
        @Override
        public void run() {
            List<Map<Object, Object>> batchOfRecords = blueskyMessageBatcher.getBatchedRecordsFromBuffer();
            applicationEventPublisher.publishEvent(new BlueskyBatchEvent(this, batchOfRecords));
        }
    };
}
