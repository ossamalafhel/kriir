package com.kriir.platform.mapper;

import com.kriir.platform.dto.SecurityIncidentDto;
import com.kriir.platform.dto.CreateSecurityIncidentRequest;
import com.kriir.platform.model.SecurityIncident;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("SecurityIncidentMapper Tests")
class SecurityIncidentMapperTest {
    
    @Inject
    SecurityIncidentMapper securityIncidentMapper;
    
    @Test
    @DisplayName("Should map SecurityIncident to SecurityIncidentDto correctly")
    void testToDto() {
        SecurityIncident incident = new SecurityIncident("Title", "TYPE", "HIGH", 1.0, 2.0, "Test incident description");
        incident.id = "test-id";
        incident.status = "OPEN";
        incident.description = "Test description";
        incident.affectedAssetId = "asset-id";
        incident.assignedTo = "user@example.com";
        LocalDateTime detected = LocalDateTime.now();
        LocalDateTime resolved = detected.plusHours(1);
        incident.detectedAt = detected;
        incident.resolvedAt = resolved;
        
        SecurityIncidentDto dto = securityIncidentMapper.toDto(incident);
        
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo("test-id");
        assertThat(dto.title()).isEqualTo("Title");
        assertThat(dto.type()).isEqualTo("TYPE");
        assertThat(dto.severity()).isEqualTo("HIGH");
        assertThat(dto.status()).isEqualTo("OPEN");
        assertThat(dto.x()).isEqualTo(1.0);
        assertThat(dto.y()).isEqualTo(2.0);
        assertThat(dto.description()).isEqualTo("Test description");
        assertThat(dto.affectedAssetId()).isEqualTo("asset-id");
        assertThat(dto.detectedAt()).isEqualTo(detected);
        assertThat(dto.resolvedAt()).isEqualTo(resolved);
        assertThat(dto.assignedTo()).isEqualTo("user@example.com");
    }
    
    @Test
    @DisplayName("Should handle null SecurityIncident")
    void testToDtoWithNull() {
        SecurityIncidentDto dto = securityIncidentMapper.toDto(null);
        assertThat(dto).isNull();
    }
    
    @Test
    @DisplayName("Should map CreateSecurityIncidentRequest to SecurityIncident correctly")
    void testToEntity() {
        CreateSecurityIncidentRequest request = new CreateSecurityIncidentRequest(
            "Title", "TYPE", "HIGH", 1.0, 2.0, "Description", "asset-id"
        );
        
        SecurityIncident incident = securityIncidentMapper.toEntity(request);
        
        assertThat(incident).isNotNull();
        assertThat(incident.id).isNotNull(); // Generated UUID
        assertThat(incident.title).isEqualTo("Title");
        assertThat(incident.type).isEqualTo("TYPE");
        assertThat(incident.severity).isEqualTo("HIGH");
        assertThat(incident.x).isEqualTo(1.0);
        assertThat(incident.y).isEqualTo(2.0);
        assertThat(incident.description).isEqualTo("Description");
        assertThat(incident.affectedAssetId).isEqualTo("asset-id");
        assertThat(incident.status).isEqualTo("OPEN"); // Default
        assertThat(incident.detectedAt).isNotNull(); // Default
    }
    
    @Test
    @DisplayName("Should handle null CreateSecurityIncidentRequest")
    void testToEntityWithNull() {
        SecurityIncident incident = securityIncidentMapper.toEntity(null);
        assertThat(incident).isNull();
    }
    
    @Test
    @DisplayName("Should update SecurityIncident from CreateSecurityIncidentRequest correctly")
    void testUpdateEntity() {
        SecurityIncident incident = new SecurityIncident("OldTitle", "OLD_TYPE", "LOW", 0.0, 0.0, "Old description");
        CreateSecurityIncidentRequest request = new CreateSecurityIncidentRequest(
            "NewTitle", "NEW_TYPE", "HIGH", 1.0, 2.0, "New description", "new-asset"
        );
        
        securityIncidentMapper.updateEntity(incident, request);
        
        assertThat(incident.title).isEqualTo("NewTitle");
        assertThat(incident.type).isEqualTo("NEW_TYPE");
        assertThat(incident.severity).isEqualTo("HIGH");
        assertThat(incident.x).isEqualTo(1.0);
        assertThat(incident.y).isEqualTo(2.0);
        assertThat(incident.description).isEqualTo("New description");
        assertThat(incident.affectedAssetId).isEqualTo("new-asset");
    }
    
    @Test
    @DisplayName("Should handle null parameters in updateEntity")
    void testUpdateEntityWithNulls() {
        SecurityIncident incident = new SecurityIncident("Title", "TYPE", "HIGH", 1.0, 2.0, "Test incident description");
        CreateSecurityIncidentRequest request = new CreateSecurityIncidentRequest(
            "NewTitle", "NEW_TYPE", "LOW", 3.0, 4.0, "desc", "asset"
        );
        
        // Test null incident
        securityIncidentMapper.updateEntity(null, request);
        // Should not throw exception
        
        // Test null request
        securityIncidentMapper.updateEntity(incident, null);
        // Should not modify incident
        assertThat(incident.title).isEqualTo("Title");
        assertThat(incident.type).isEqualTo("TYPE");
    }
}