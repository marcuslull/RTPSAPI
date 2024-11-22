package com.marcuslull.rtpsapi.listeners.bluesky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class BlueskyConstants {
    public static final String POST_FILTER = "app.bsky.feed.post/";
    public static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // BlueSky firehose
    public static final String FIREHOSE_WORKER_SCRIPT_RUN_COMMAND = "node";
    public static final String FIREHOSE_WORKER_SCRIPT_LOCATION = "./firehoseClient/firehose.js";
    public static final int FIREHOSE_WORKER_THREAD_POOL_SIZE = 1;
    public static final int FIREHOSE_WORKER_LIFESPAN_SECONDS = 10;
    public static final int FIREHOSE_WORKER_STARTUP_DELAY = 0;
    public static final int FIREHOSE_THREAD_POOL_LIFESPAN_SECONDS = 300;
    public static final int FIREHOSE_WORKER_INTERVAL_SECONDS = 2;
    public static final int FIREHOSE_MESSAGE_BUFFER_MIN_COUNT = 100;
    public static final int FIREHOSE_BUFFER_WAIT_TIME_MS = 2000;
    public static final int LISTENER_NUMBER_OF_MESSAGES_TO_GET = 100;

    // BlueSky Redis Stream
    public static final String BLUESKY_REDIS_STREAM_KEY = "bskyFirehose";
    public static final String BLUESKY_REDIS_GROUP_NAME = "bskyGroup";
    public static final String BLUESKY_REDIS_CONSUMER_NAME = "bskyConsumer";
}
