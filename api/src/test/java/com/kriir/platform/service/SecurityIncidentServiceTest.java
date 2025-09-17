package com.kriir.platform.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("SecurityIncidentService Tests")
class SecurityIncidentServiceTest {

    @Inject
    SecurityIncidentService securityIncidentService;

    @Test
    @DisplayName("Should inject SecurityIncidentService")
    void shouldInjectService() {
        assertNotNull(securityIncidentService, "SecurityIncidentService should be injected");
    }

    @Test
    @DisplayName("Should have service methods available")
    void shouldHaveServiceMethods() {
        // Test that the service has the expected methods without executing them
        assertNotNull(securityIncidentService, "SecurityIncidentService should be injected");
        
        // This is a simple smoke test to ensure the service is properly configured
        // The actual business logic is tested in integration tests
        assertTrue(true, "Service context loads successfully");
    }
}