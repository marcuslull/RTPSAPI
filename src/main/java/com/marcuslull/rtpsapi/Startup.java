package com.marcuslull.rtpsapi;

import com.marcuslull.rtpsapi.listeners.BlueskyPipeline;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Startup implements CommandLineRunner {
    private final BlueskyPipeline blueskyPipeline;

    public Startup(BlueskyPipeline blueskyPipeline) {
        this.blueskyPipeline = blueskyPipeline;
    }

    @Override
    public void run(String... args) {
        blueskyPipeline.start();
    }

    @PreDestroy
    public void cleanup() {
        blueskyPipeline.stop();
    }
}