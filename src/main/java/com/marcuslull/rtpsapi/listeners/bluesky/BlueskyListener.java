package com.marcuslull.rtpsapi.listeners.bluesky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.marcuslull.rtpsapi.listeners.bluesky.model.BlueskyPost;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;

@Service
public class BlueskyListener {
    private final String SCRIPT_COMMAND = "node";
    private final String SCRIPT_LOCATION = "./firehoseClient/firehose.js";
    private final String POST_FILTER = "app.bsky.feed.post/";
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public void connect() throws IOException {
        Process process = new ProcessBuilder(SCRIPT_COMMAND, SCRIPT_LOCATION).start();
        try (BufferedReader reader = process.inputReader()) {
            reader.lines().forEach(line -> {
                BlueskyPost post;
                String prettyPost;
                try {
                    post = mapper.readValue(line, BlueskyPost.class);
                    prettyPost = mapper.writeValueAsString(post);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (post.ops() != null && post.ops().path().contains(POST_FILTER)) {
                    System.out.println(prettyPost);
                }
            });
        }
    }
}

