package com.marcuslull.rtpsapi.listeners.bluesky;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class BlueskyConfiguration {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> blueskyRedisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory());
        template.setDefaultSerializer(StringRedisSerializer.UTF_8);
        template.afterPropertiesSet();
        return template;
    }

}
