package com.marcuslull.rtpsapi.listeners.bluesky.service;

import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.marcuslull.rtpsapi.listeners.bluesky.model.BlueskyConstants.*;

@Service
public class BlueskyMessageBatcher {

    private final RedisTemplate<String, String> blueskyRedisTemplate;

    public BlueskyMessageBatcher(RedisTemplate<String, String> blueskyRedisTemplate) {
        this.blueskyRedisTemplate = blueskyRedisTemplate;
    }

    public List<Map<Object, Object>> getBatchedRecordsFromBuffer() {
        sendBufferFillCommand();
        List<MapRecord<String, Object, Object>> records = getRecords();
        removeBatchedRecordsFromBuffer(records);
        return unwrapRecords(records);
    }

    private List<MapRecord<String, Object, Object>> getRecords() {
        Consumer consumer = Consumer.from(BLUESKY_REDIS_GROUP_NAME, BLUESKY_REDIS_CONSUMER_NAME);
        StreamReadOptions options = StreamReadOptions.empty().count(LISTENER_NUMBER_OF_MESSAGES_TO_GET);
        StreamOffset<String> offset = StreamOffset.create(BLUESKY_REDIS_STREAM_KEY, ReadOffset.lastConsumed());

        @SuppressWarnings("unchecked")
        List<MapRecord<String, Object, Object>> records = blueskyRedisTemplate.opsForStream().read(consumer, options, offset);
        return records;
    }

    private List<Map<Object, Object>> unwrapRecords(List<MapRecord<String, Object, Object>> records) {
        return records.stream().map(Record::getValue).toList();
    }

    private void removeBatchedRecordsFromBuffer(List<MapRecord<String, Object, Object>> records) {
        if (records != null && !records.isEmpty()) {
            records.forEach(record -> blueskyRedisTemplate
                    .boundStreamOps(BLUESKY_REDIS_STREAM_KEY)
                    .delete(String.valueOf(record.getId())));
        }
    }

    private void sendBufferFillCommand() {
        if (BlueskyFirehoseBuffer.length() < FIREHOSE_MESSAGE_BUFFER_MIN_COUNT) {
            BlueskyFirehoseBuffer.fill();
            try {
                Thread.sleep(FIREHOSE_BUFFER_WAIT_TIME_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
