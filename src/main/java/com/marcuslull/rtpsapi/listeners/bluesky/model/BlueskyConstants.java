package com.marcuslull.rtpsapi.listeners.bluesky.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class BlueskyConstants {
    public static final String POST_FILTER = "app.bsky.feed.post/";
    public static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // BlueSky firehose runtime constants
    public static final String FIREHOSE_WORKER_SCRIPT_RUN_COMMAND = "node";
    public static final String FIREHOSE_WORKER_SCRIPT_LOCATION = "./firehoseClient/firehose.js";
    public static final int FIREHOSE_WORKER_LIFESPAN_SECONDS = 2;
    public static final int FIREHOSE_MESSAGE_BUFFER_MIN_COUNT = 1000;
    public static final int FIREHOSE_BUFFER_WAIT_TIME_MS = 2000;
    public static final int LISTENER_NUMBER_OF_MESSAGES_TO_GET = 500;

    // BlueSky Redis Stream runtime constants
    public static final String BLUESKY_REDIS_STREAM_KEY = "bskyFirehose";
    public static final String BLUESKY_REDIS_GROUP_NAME = "bskyGroup";
    public static final String BLUESKY_REDIS_CONSUMER_NAME = "bskyConsumer";
}
