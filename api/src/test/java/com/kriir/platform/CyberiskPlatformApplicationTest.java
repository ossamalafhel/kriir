package com.kriir.platform;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("KriirPlatformApplication Tests")
class KriirPlatformApplicationTest {

    @Test
    @DisplayName("Should have main method")
    void mainMethodExists() {
        // Test that the main method exists and the class can be loaded
        assertThat(KriirPlatformApplication.class).isNotNull();
        assertThat(KriirPlatformApplication.class.getDeclaredMethods())
            .anyMatch(method -> method.getName().equals("main"));
    }

    @Test
    @DisplayName("Should create application instance")
    void applicationInstantiates() {
        KriirPlatformApplication app = new KriirPlatformApplication();
        assertThat(app).isNotNull();
    }

    @Test
    @DisplayName("Context loads")
    void contextLoads() {
        // This test verifies that the Quarkus context can be loaded
    }
}