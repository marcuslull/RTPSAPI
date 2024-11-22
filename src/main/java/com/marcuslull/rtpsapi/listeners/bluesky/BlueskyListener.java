package com.marcuslull.rtpsapi.listeners.bluesky;

import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.marcuslull.rtpsapi.listeners.bluesky.BlueskyConstants.*;

@Service
public class BlueskyListener {

    private final RedisTemplate<String, String> blueskyRedisTemplate;
    private final BlueskyFirehose blueskyFirehose;

    public BlueskyListener(RedisTemplate<String, String> blueskyRedisTemplate, BlueskyFirehose blueskyFirehose) {
        this.blueskyRedisTemplate = blueskyRedisTemplate;
        this.blueskyFirehose = blueskyFirehose;
    }

    public List<MapRecord<String, Object, Object>> getRecords() {
        StreamReadOptions options;
        setGroupNameIfNeeded();
        if (getStreamLength() < FIREHOSE_MESSAGE_BUFFER_MIN_COUNT) {
            System.out.println("Not enough messages in the stream. Starting firehose.");
            blueskyFirehose.connect();
            System.out.println("waiting for messages in the Redis stream...");
            try {
                Thread.sleep(FIREHOSE_BUFFER_WAIT_TIME_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        options = StreamReadOptions.empty().count(LISTENER_NUMBER_OF_MESSAGES_TO_GET);
        System.out.println("Gathering messages...");
        List<MapRecord<String, Object, Object>> records = blueskyRedisTemplate.opsForStream().read(
                Consumer.from(BLUESKY_REDIS_GROUP_NAME, BLUESKY_REDIS_CONSUMER_NAME),
                options,
                StreamOffset.create(BLUESKY_REDIS_STREAM_KEY, ReadOffset.lastConsumed()));
        if (records != null && !records.isEmpty()) {
            removeRecords(records);
            return records;
        }
        return null;
    }

    private void setGroupNameIfNeeded() {
        StreamInfo.XInfoGroups groups = blueskyRedisTemplate.opsForStream().groups(BLUESKY_REDIS_STREAM_KEY);
        if (groups.isEmpty()) {
            blueskyRedisTemplate.opsForStream().createGroup(BLUESKY_REDIS_STREAM_KEY, BLUESKY_REDIS_GROUP_NAME);
        }
    }

    private long getStreamLength() {
        return blueskyRedisTemplate.opsForStream().info(BLUESKY_REDIS_STREAM_KEY).streamLength();
    }

    private void removeRecords(List<MapRecord<String, Object, Object>> records) {
        records.forEach(record -> blueskyRedisTemplate.boundStreamOps(BLUESKY_REDIS_STREAM_KEY).delete(String.valueOf(record.getId())));
    }
}
