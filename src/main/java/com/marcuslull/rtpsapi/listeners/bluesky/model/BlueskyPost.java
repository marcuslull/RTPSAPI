package com.marcuslull.rtpsapi.listeners.bluesky.model;

public record BlueskyPost(
        String repo,
        BlueskyOp ops
) {
}
