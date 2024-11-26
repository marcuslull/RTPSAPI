package com.marcuslull.rtpsapi.listeners.bluesky.model;

import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

public class BlueskyBatchEvent extends ApplicationEvent {

    List<Map<Object, Object>> batchOfRecords;

    public BlueskyBatchEvent(Object source, List<Map<Object, Object>> batchOfRecords) {
        super(source);
        this.batchOfRecords = batchOfRecords;
    }

    public List<Map<Object, Object>> getBatchOfRecords() {
        return batchOfRecords;
    }
}
