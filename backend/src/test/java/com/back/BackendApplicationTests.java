package com.back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // 테스트 프로필 활성화
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // Spring Context 로딩 테스트 - H2 인메모리 DB 사용
    }

}
