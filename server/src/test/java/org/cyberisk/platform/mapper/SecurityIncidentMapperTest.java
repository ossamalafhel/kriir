package org.cyberisk.platform.mapper;

import org.cyberisk.platform.dto.SecurityIncidentDto;
import org.cyberisk.platform.dto.CreateSecurityIncidentRequest;
import org.cyberisk.platform.model.SecurityIncident;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SecurityIncidentMapper Tests")
class SecurityIncidentMapperTest {
    
    private SecurityIncidentMapper securityIncidentMapper;
    
    @BeforeEach
    void setUp() {
        securityIncidentMapper = new SecurityIncidentMapper();
    }
    
    @Test
    @DisplayName("Should map SecurityIncident to SecurityIncidentDto correctly")
    void testToDto() {
        SecurityIncident incident = new SecurityIncident("Title", "TYPE", "HIGH", 1.0, 2.0);
        incident.setId("test-id");
        incident.setStatus("OPEN");
        incident.setDescription("Test description");
        incident.setAffectedAssetId("asset-id");
        incident.setAssignedTo("user@example.com");
        LocalDateTime detected = LocalDateTime.now();
        LocalDateTime resolved = detected.plusHours(1);
        incident.setDetectedAt(detected);
        incident.setResolvedAt(resolved);
        
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
        assertThat(incident.getId()).isNotNull(); // Generated UUID
        assertThat(incident.getTitle()).isEqualTo("Title");
        assertThat(incident.getType()).isEqualTo("TYPE");
        assertThat(incident.getSeverity()).isEqualTo("HIGH");
        assertThat(incident.getX()).isEqualTo(1.0);
        assertThat(incident.getY()).isEqualTo(2.0);
        assertThat(incident.getDescription()).isEqualTo("Description");
        assertThat(incident.getAffectedAssetId()).isEqualTo("asset-id");
        assertThat(incident.getStatus()).isEqualTo("OPEN"); // Default
        assertThat(incident.getDetectedAt()).isNotNull(); // Default
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
        SecurityIncident incident = new SecurityIncident("OldTitle", "OLD_TYPE", "LOW", 0.0, 0.0);
        CreateSecurityIncidentRequest request = new CreateSecurityIncidentRequest(
            "NewTitle", "NEW_TYPE", "HIGH", 1.0, 2.0, "New description", "new-asset"
        );
        
        securityIncidentMapper.updateEntity(incident, request);
        
        assertThat(incident.getTitle()).isEqualTo("NewTitle");
        assertThat(incident.getType()).isEqualTo("NEW_TYPE");
        assertThat(incident.getSeverity()).isEqualTo("HIGH");
        assertThat(incident.getX()).isEqualTo(1.0);
        assertThat(incident.getY()).isEqualTo(2.0);
        assertThat(incident.getDescription()).isEqualTo("New description");
        assertThat(incident.getAffectedAssetId()).isEqualTo("new-asset");
    }
    
    @Test
    @DisplayName("Should handle null parameters in updateEntity")
    void testUpdateEntityWithNulls() {
        SecurityIncident incident = new SecurityIncident("Title", "TYPE", "HIGH", 1.0, 2.0);
        CreateSecurityIncidentRequest request = new CreateSecurityIncidentRequest(
            "NewTitle", "NEW_TYPE", "LOW", 3.0, 4.0, "desc", "asset"
        );
        
        // Test null incident
        securityIncidentMapper.updateEntity(null, request);
        // Should not throw exception
        
        // Test null request
        securityIncidentMapper.updateEntity(incident, null);
        // Should not modify incident
        assertThat(incident.getTitle()).isEqualTo("Title");
        assertThat(incident.getType()).isEqualTo("TYPE");
    }
}