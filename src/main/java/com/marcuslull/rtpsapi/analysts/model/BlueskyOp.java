package com.marcuslull.rtpsapi.analysts.model;

public record BlueskyOp(
        String action,
        String path,
        String cid,
        Object record
) {
}
