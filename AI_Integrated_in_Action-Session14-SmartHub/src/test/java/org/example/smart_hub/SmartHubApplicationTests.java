package org.example.smart_hub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmartHubApplicationTests {

    static {
        SmartHubApplication.loadDotEnv();
    }

    @Test
    void contextLoads() {
    }

}
