package it.vitalegi.cosucce;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class CosucceApplicationTests {

    @DisplayName("Application starts")
    @Test
    void contextLoads() {
    }

}
