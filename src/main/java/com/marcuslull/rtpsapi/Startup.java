package com.marcuslull.rtpsapi;

import com.marcuslull.rtpsapi.listeners.bluesky.BlueskyFirehose;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

@Component
public class Startup implements CommandLineRunner {
    private final BlueskyFirehose blueskyFirehose;

    public Startup(BlueskyFirehose blueskyFirehose) {
        this.blueskyFirehose = blueskyFirehose;
    }

    @Override
    public void run(String... args) {
        ScheduledExecutorService scheduledExecutorService = blueskyFirehose.connect(
                1,
                5,
                1,
                0,
                2
        );
    }
}