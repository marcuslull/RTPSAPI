package com.marcuslull.rtpsapi;

import com.marcuslull.rtpsapi.listeners.bluesky.BlueskyListener;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Startup implements CommandLineRunner {
    private final BlueskyListener blueskyListener;

    public Startup(BlueskyListener blueskyListener) {
        this.blueskyListener = blueskyListener;
    }

    @Override
    public void run(String... args) {
        List<MapRecord<String, Object, Object>> records = blueskyListener.getRecords();
        System.out.println("Size of records (should be 100): " + records.size());
        for (MapRecord<String, Object, Object> record : records) {
            System.out.println("Bluesky Listener - Message: " + record.getId());
        }
    }
}