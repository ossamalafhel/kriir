package org.cyberisk.platform;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

@DisplayName("CyberiskPlatformApplication Main Method Test")
class CyberiskPlatformMainTest {

    @Test
    @DisplayName("Should run main method")
    void mainMethodRuns() {
        try (MockedStatic<SpringApplication> springApp = mockStatic(SpringApplication.class)) {
            CyberiskPlatformApplication.main(new String[]{});
            springApp.verify(() -> SpringApplication.run(CyberiskPlatformApplication.class, new String[]{}));
        }
    }
}