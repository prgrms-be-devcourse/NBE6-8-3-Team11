package com.back;

import com.back.config.EmbeddedRedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(EmbeddedRedisConfig.class)
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // Spring Context loading test with H2 in-memory database and embedded Redis
    }

}
