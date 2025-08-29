package com.back.global.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(connectionFactory)

        // JSON 직렬화 설정
        val objectMapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        val jsonSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        template.apply {
            setKeySerializer(StringRedisSerializer())
            setValueSerializer(jsonSerializer)
            setHashKeySerializer(StringRedisSerializer())
            setHashValueSerializer(jsonSerializer)
            afterPropertiesSet()
        }
        
        return template
    }

    @Bean
    fun redisMessageListenerContainer(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            setConnectionFactory(connectionFactory)
        }
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(JavaTimeModule())
        }
    }
}
