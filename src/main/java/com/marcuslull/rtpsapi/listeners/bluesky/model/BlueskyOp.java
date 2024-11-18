package com.marcuslull.rtpsapi.listeners.bluesky.model;

public record BlueskyOp(
        String action,
        String path,
        String cid,
        Object record
) {
}
