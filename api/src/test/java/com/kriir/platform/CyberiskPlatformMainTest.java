package com.kriir.platform;

import io.quarkus.runtime.Quarkus;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

@QuarkusTest
@DisplayName("KriirPlatformApplication Main Method Test")
class CyberiskPlatformMainTest {

    @Test
    @DisplayName("Should run main method")
    void mainMethodRuns() {
        try (MockedStatic<Quarkus> quarkusApp = mockStatic(Quarkus.class)) {
            KriirPlatformApplication.main(new String[]{});
            quarkusApp.verify(() -> Quarkus.run(KriirPlatformApplication.class, new String[]{}));
        }
    }
}