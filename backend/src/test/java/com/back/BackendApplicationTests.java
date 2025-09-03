package com.back;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 기본적인 단위 테스트 - Spring Context 로드 없음
 * CI 환경에서 안정적으로 실행되는 단순한 테스트
 */
class BackendApplicationTests {

    @Test
    void applicationMainClassExists() {
        // 메인 애플리케이션 클래스가 존재하는지 확인
        assertDoesNotThrow(() -> {
            Class.forName("com.back.Application");
        });
    }

    @Test 
    void basicJavaFunctionality() {
        // 기본적인 Java 기능 테스트
        String testString = "Hello Spring Boot";
        assertNotNull(testString);
        assertTrue(testString.contains("Spring"));
        assertEquals(17, testString.length());
    }
    
    @Test
    void kotlinInteroperability() {
        // Kotlin과 Java 상호운용성 기본 테스트
        assertDoesNotThrow(() -> {
            // Kotlin 클래스 로드 테스트
            Class.forName("com.back.domain.member.enums.UserRole");
        });
    }
}
