package com.kriir.platform;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

@DisplayName("KriirPlatformApplication Main Method Test")
class CyberiskPlatformMainTest {

    @Test
    @DisplayName("Should run main method")
    void mainMethodRuns() {
        try (MockedStatic<SpringApplication> springApp = mockStatic(SpringApplication.class)) {
            KriirPlatformApplication.main(new String[]{});
            springApp.verify(() -> SpringApplication.run(KriirPlatformApplication.class, new String[]{}));
        }
    }
}