package com.project.hub.config;


import static java.time.Duration.ofMillis;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private Integer port;

  @Value("${spring.data.redis.timeout}")
  private Long timeout;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(host);
    config.setPort(port);

    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
        .commandTimeout(ofMillis(timeout))
        .build();

    return new LettuceConnectionFactory(config, clientConfig);
  }

  // 분산락을 사용하기 위한 RedisTemplate
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    // 사람의 수를 확인하기위한
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

    redisTemplate.setConnectionFactory(redisConnectionFactory());

    // Key : Value 형식으로 사용할 경우 시리얼라이저
    redisTemplate.setKeySerializer(new StringRedisSerializer());
//    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    // Hash를 사용할 경우 시리얼라이저
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(new StringRedisSerializer());

    // 모든 경우
    redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

    return redisTemplate;
  }

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    return RedisCacheManager.builder(redisConnectionFactory)
        // TTL 60초
        .cacheDefaults(redisCacheConfiguration(60))
        .withCacheConfiguration("project_LATEST", redisCacheConfiguration(600))
        .withCacheConfiguration("project_POPULAR", redisCacheConfiguration(600))
        .withCacheConfiguration("project_COMMENT", redisCacheConfiguration(600))
        .withCacheConfiguration("allProject_LATEST", redisCacheConfiguration(600))
        .withCacheConfiguration("allProject_POPULAR", redisCacheConfiguration(600))
        .withCacheConfiguration("allProject_COMMENT", redisCacheConfiguration(600))
        .build();
  }

  private RedisCacheConfiguration redisCacheConfiguration(int seconds) {
    return RedisCacheConfiguration
        .defaultCacheConfig()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new StringRedisSerializer())
        )
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer())
        )
        .entryTtl(Duration.ofSeconds(seconds));
  }
}
