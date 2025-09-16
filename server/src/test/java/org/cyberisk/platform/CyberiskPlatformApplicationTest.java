package org.cyberisk.platform;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.main.web-application-type=none"})
@DisplayName("CyberiskPlatformApplication Tests")
class CyberiskPlatformApplicationTest {

    @Test
    @DisplayName("Should have main method")
    void mainMethodExists() {
        // Test that the main method exists and the class can be loaded
        assertThat(CyberiskPlatformApplication.class).isNotNull();
        assertThat(CyberiskPlatformApplication.class.getDeclaredMethods())
            .anyMatch(method -> method.getName().equals("main"));
    }

    @Test
    @DisplayName("Should create application instance")
    void applicationInstantiates() {
        CyberiskPlatformApplication app = new CyberiskPlatformApplication();
        assertThat(app).isNotNull();
    }

    @Test
    @DisplayName("Context loads")
    void contextLoads() {
        // This test verifies that the Spring context can be loaded
    }
}