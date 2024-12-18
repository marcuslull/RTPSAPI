package com.marcuslull.rtpsapi.analysts.model;

public record BlueskyRecord(
        Object text,
        Object $type,
        Object embed,
        Object[] images,
        Object[] langs,
        Object reply,
        Object parent,
        Object uri,
        Object createdAt,
        Object[] facets
) {
}
