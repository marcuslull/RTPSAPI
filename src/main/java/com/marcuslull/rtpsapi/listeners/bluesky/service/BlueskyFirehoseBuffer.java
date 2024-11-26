package com.marcuslull.rtpsapi.listeners.bluesky.service;

import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.marcuslull.rtpsapi.listeners.bluesky.model.BlueskyConstants.*;

@Service
public class BlueskyFirehoseBuffer {

    private static RedisTemplate<String, String> blueskyRedisTemplate;
    private static Process worker;

    public BlueskyFirehoseBuffer(RedisTemplate<String, String> blueskyRedisTemplate) {
        BlueskyFirehoseBuffer.blueskyRedisTemplate = blueskyRedisTemplate;
        setRedisGroupNameIfNeeded();
    }

    public static void fill() {
        try {
            if (worker != null && worker.isAlive()) { // if its already running just wait
                worker.waitFor(FIREHOSE_WORKER_LIFESPAN_SECONDS, TimeUnit.SECONDS);
            }
            else { // otherwise start it up and wait
                worker = new ProcessBuilder(FIREHOSE_WORKER_SCRIPT_RUN_COMMAND, FIREHOSE_WORKER_SCRIPT_LOCATION)
                        .inheritIO() // redirect all console.*() to System.out.*()
                        .start();
                if (!worker.waitFor(FIREHOSE_WORKER_LIFESPAN_SECONDS, TimeUnit.SECONDS)) {
                    // blocking .waitFor() to act as a worker lifespan
                    worker.destroy();
                    System.out.println("Firehose Task - batch worker destroyed");
                }
            }
        } catch (Exception e) {
            System.out.println("Firehose Task - Firehose worker error: " + e.getMessage());
        }
    }

    public static void clear() {
        if (worker != null && worker.isAlive()) {
            worker.destroy();
        }
        // sometimes it does not trim all the way to 0 so running twice. Same behavior on Redis-cli.
        blueskyRedisTemplate.opsForStream().trim(BLUESKY_REDIS_STREAM_KEY, 0);
        blueskyRedisTemplate.opsForStream().trim(BLUESKY_REDIS_STREAM_KEY, 0);
    }

    public static long length() {
        return blueskyRedisTemplate.opsForStream().info(BLUESKY_REDIS_STREAM_KEY).streamLength();
    }

    private static void setRedisGroupNameIfNeeded() {
        StreamInfo.XInfoGroups groups = blueskyRedisTemplate.opsForStream().groups(BLUESKY_REDIS_STREAM_KEY);
        if (groups.isEmpty()) {
            blueskyRedisTemplate.opsForStream().createGroup(BLUESKY_REDIS_STREAM_KEY, BLUESKY_REDIS_GROUP_NAME);
        }
    }
}