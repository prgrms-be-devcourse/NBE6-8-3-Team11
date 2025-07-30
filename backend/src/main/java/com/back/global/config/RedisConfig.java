package com.back.global.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!railway") // railway 프로필이 아닐 때만 활성화
@ConditionalOnProperty(name = "spring.data.redis.url")
public class RedisConfig {
    // Redis 설정이 필요한 경우 여기에 추가
}
