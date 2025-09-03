package com.back;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * 모든 테스트의 기본 클래스
 * H2 인메모리 DB와 Redis 비활성화 설정이 적용됨
 */
@SpringBootTest
@TestPropertySource(properties = {
    // H2 인메모리 데이터베이스
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    
    // JPA 설정
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.show-sql=false",
    
    // Redis 완전 비활성화
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
    
    // JWT 테스트 설정
    "jwt.secret=testSecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongAndSecureEnoughForTesting123456789",
    "jwt.access-token-expiration=PT1H",
    "jwt.refresh-token-expiration=PT24H",
    
    // 기타 설정
    "cookie.secure=false",
    "websocket.allowed-origins=http://localhost:3000,http://localhost:8080",
    
    // 로깅
    "logging.level.root=ERROR"
})
public abstract class TestBase {
}
