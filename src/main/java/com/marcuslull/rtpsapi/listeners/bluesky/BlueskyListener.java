package com.marcuslull.rtpsapi.listeners.bluesky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class BlueskyListener {

    private final String POST_FILTER = "app.bsky.feed.post/";
    private final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final String REDIS_STREAM_KEY = "bskyFirehose";
    private final String REDIS_GROUP_NAME = "bskyGroup";
    private final String REDIS_CONSUMER_NAME = "bskyConsumer";











//        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
//        connectionFactory.afterPropertiesSet();
//
//        RedisTemplate<String, String> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        template.setDefaultSerializer(StringRedisSerializer.UTF_8);
//        template.afterPropertiesSet();
//
//        StreamInfo.XInfoGroups groups = template.opsForStream().groups(REDIS_STREAM_KEY);
//        if (groups.isEmpty()) {
//            template.opsForStream().createGroup(REDIS_STREAM_KEY, REDIS_GROUP_NAME);
//        }
//
//        System.out.println(template.opsForStream().info(REDIS_STREAM_KEY));
//
//
//        StreamReadOptions options = StreamReadOptions.empty().count(200);
//        List<MapRecord<String, Object, Object>> records = template.opsForStream().read(
//                Consumer.from(REDIS_GROUP_NAME, REDIS_CONSUMER_NAME),
//                options,
//                StreamOffset.create(REDIS_STREAM_KEY, ReadOffset.lastConsumed())
//        );
//
//        // 5. Process the messages
//        for (MapRecord<String, Object, Object> record : records) {
//
//            System.out.println(record.getValue());
//
//            // Acknowledge the message after processing
//            template.opsForStream().acknowledge(REDIS_STREAM_KEY, REDIS_GROUP_NAME, record.getId());
//        }
//
//
//
//        process.destroy();
//        process.onExit().thenRun(() -> {
//            System.out.println("process is alive = " + process.isAlive());
//            connectionFactory.destroy();
//        });

























}
