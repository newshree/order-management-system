package com.ecom.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * RedisConfig - Configuration for Redis integration.
 *
 * Purpose:
 * - Configure Redis connection
 * - Set up serialization for cache data
 * - Enable Spring Data Redis features
 * - Configure TTL management
 *
 * Features:
 * - Automatic serialization/deserialization
 * - JSON-based storage for complex objects
 * - TTL support (7 days for carts)
 * - String-based keys
 *
 */
@Slf4j
@Configuration
public class RedisConfig {

    /**
     * Configures RedisTemplate with custom serialization.
     *
     * Uses:
     * - String serialization for keys
     * - JSON serialization for values
     *
     * This enables storing complex objects (like RedisCart) as JSON in Redis.
     *
     * @param connectionFactory the Redis connection factory
     * @return configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) { //manages connection to Redis server
        log.debug("Configuring RedisTemplate with custom serialization");

        RedisTemplate<String, Object> template = new RedisTemplate<>(); //Creating Redis operation handler
        template.setConnectionFactory(connectionFactory); //Connecting template to actual Redis server
        log.trace("Redis connection factory set");

        // Key serializer: String
        // Redis internally stores: byte[], not Java objects directly. So Java objects must be converted. 
        // This conversion is called Serialization. This converts keys into readable strings.

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer); // "cart:101"
        template.setHashKeySerializer(stringRedisSerializer); // Used when Redis HASH structure is used. HSET cart:101 item1 value1
        log.debug("String serializer configured for keys");

        // Value serializer: JSON
        // This converts Java objects into JSON. This allows storing complex objects (like RedisCart) as JSON strings in Redis.

        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        log.debug("JSON serializer configured for values");

        template.afterPropertiesSet(); // Finalize the setup of the template (initializes internal structures)
        log.info("RedisTemplate configured successfully");

        return template; // Return the configured RedisTemplate bean
    }
}
