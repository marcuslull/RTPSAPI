package com.marcuslull.rtpsapi;

import com.marcuslull.rtpsapi.listeners.bluesky.BlueskyListener;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Startup implements CommandLineRunner {
    private final BlueskyListener blueskyListener;

    public Startup(BlueskyListener blueskyListener) {
        this.blueskyListener = blueskyListener;
    }

    @Override
    public void run(String... args) throws IOException {
        blueskyListener.connect();
    }
}