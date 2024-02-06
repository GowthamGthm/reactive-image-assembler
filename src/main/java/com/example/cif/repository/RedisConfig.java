package com.example.cif.repository;

import com.example.cif.model.CifRequest;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.*;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, CifRequest> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        RedisSerializationContext<String, CifRequest> serializationContext =
                RedisSerializationContext.<String, CifRequest>newSerializationContext()
                        .key(RedisSerializer.string())
                        .value(new Jackson2JsonRedisSerializer<>(CifRequest.class))
                        .hashKey(RedisSerializer.string())
                        .hashValue(new Jackson2JsonRedisSerializer<>(CifRequest.class))
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

}