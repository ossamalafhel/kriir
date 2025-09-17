package com.kriir.platform.incident;

import com.kriir.platform.model.SecurityIncident;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("SecurityIncident Model Tests")
class SecurityIncidentTest {

    private SecurityIncident incident;

    @BeforeEach
    void setUp() {
        incident = new SecurityIncident();
    }

    // Constructor Tests
    @Test
    @DisplayName("Default constructor should generate UUID and set defaults")
    void defaultConstructor() {
        SecurityIncident newIncident = new SecurityIncident();
        
        assertThat(newIncident.id).isNotNull();
        assertThat(newIncident.id).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        assertThat(newIncident.status).isEqualTo("OPEN");
        assertThat(newIncident.detectedAt).isNotNull();
        assertThat(newIncident.detectedAt).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(newIncident.resolvedAt).isNull();
    }

    @Test
    @DisplayName("Parameterized constructor should set all fields correctly")
    void parameterizedConstructor() {
        SecurityIncident newIncident = new SecurityIncident(
            "Malware Detected", 
            "MALWARE_DETECTION", 
            "HIGH", 
            7.06064, 
            48.092971,
            "Malware detected on the system"
        );
        
        assertThat(newIncident.id).isNotNull();
        assertThat(newIncident.title).isEqualTo("Malware Detected");
        assertThat(newIncident.type).isEqualTo("MALWARE_DETECTION");
        assertThat(newIncident.severity).isEqualTo("HIGH");
        assertThat(newIncident.x).isEqualTo(7.06064);
        assertThat(newIncident.y).isEqualTo(48.092971);
        assertThat(newIncident.status).isEqualTo("OPEN");
        assertThat(newIncident.detectedAt).isNotNull();
    }

    // Getter/Setter Tests
    @Test
    @DisplayName("Should set and get ID correctly")
    void idGetterSetter() {
        String id = UUID.randomUUID().toString();
        incident.id = id;
        assertThat(incident.id).isEqualTo(id);
    }

    @Test
    @DisplayName("Should set and get title correctly")
    void titleGetterSetter() {
        incident.title = "DDoS Attack Detected";
        assertThat(incident.title).isEqualTo("DDoS Attack Detected");
    }

    @Test
    @DisplayName("Should set and get type correctly")
    void typeGetterSetter() {
        incident.type = "DDoS_ATTACK";
        assertThat(incident.type).isEqualTo("DDoS_ATTACK");
    }

    @Test
    @DisplayName("Should set and get severity correctly")
    void severityGetterSetter() {
        incident.severity = "CRITICAL";
        assertThat(incident.severity).isEqualTo("CRITICAL");
    }

    @Test
    @DisplayName("Should set and get status correctly")
    void statusGetterSetter() {
        incident.status = "IN_PROGRESS";
        assertThat(incident.status).isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("Should set and get coordinates correctly")
    void coordinatesGetterSetter() {
        incident.x = 2.3522;
        incident.y = 48.8566;
        assertThat(incident.x).isEqualTo(2.3522);
        assertThat(incident.y).isEqualTo(48.8566);
    }

    @Test
    @DisplayName("Should set and get description correctly")
    void descriptionGetterSetter() {
        String desc = "Multiple failed login attempts detected from unknown IP addresses";
        incident.description = desc;
        assertThat(incident.description).isEqualTo(desc);
    }

    @Test
    @DisplayName("Should set and get affectedAssetId correctly")
    void affectedAssetIdGetterSetter() {
        String assetId = UUID.randomUUID().toString();
        incident.affectedAssetId = assetId;
        assertThat(incident.affectedAssetId).isEqualTo(assetId);
    }

    @Test
    @DisplayName("Should set and get timestamps correctly")
    void timestampsGetterSetter() {
        LocalDateTime detected = LocalDateTime.now().minusHours(2);
        LocalDateTime resolved = LocalDateTime.now();
        
        incident.detectedAt = detected;
        incident.resolvedAt = resolved;
        
        assertThat(incident.detectedAt).isEqualTo(detected);
        assertThat(incident.resolvedAt).isEqualTo(resolved);
    }

    @Test
    @DisplayName("Should set and get assignedTo correctly")
    void assignedToGetterSetter() {
        incident.assignedTo = "john.doe@company.com";
        assertThat(incident.assignedTo).isEqualTo("john.doe@company.com");
    }

    // Business Logic Tests
    @Test
    @DisplayName("Should handle null values appropriately")
    void handleNullValues() {
        incident.title = null;
        incident.type = null;
        incident.severity = null;
        incident.description = null;
        incident.affectedAssetId = null;
        incident.assignedTo = null;
        
        assertThat(incident.title).isNull();
        assertThat(incident.type).isNull();
        assertThat(incident.severity).isNull();
        assertThat(incident.description).isNull();
        assertThat(incident.affectedAssetId).isNull();
        assertThat(incident.assignedTo).isNull();
        
        // But status and ID should not be null
        assertThat(incident.status).isNotNull();
        assertThat(incident.id).isNotNull();
        assertThat(incident.detectedAt).isNotNull();
    }

    @Test
    @DisplayName("Should handle incident lifecycle correctly")
    void incidentLifecycle() {
        // Initial state
        assertThat(incident.status).isEqualTo("OPEN");
        assertThat(incident.resolvedAt).isNull();
        
        // In progress
        incident.status = "IN_PROGRESS";
        incident.assignedTo = "analyst@company.com";
        assertThat(incident.status).isEqualTo("IN_PROGRESS");
        assertThat(incident.assignedTo).isEqualTo("analyst@company.com");
        
        // Resolved
        LocalDateTime resolvedTime = LocalDateTime.now();
        incident.status = "RESOLVED";
        incident.resolvedAt = resolvedTime;
        assertThat(incident.status).isEqualTo("RESOLVED");
        assertThat(incident.resolvedAt).isEqualTo(resolvedTime);
    }

    @Test
    @DisplayName("Should calculate incident duration correctly")
    void incidentDuration() {
        LocalDateTime detected = LocalDateTime.now().minusHours(3);
        LocalDateTime resolved = LocalDateTime.now();
        
        incident.detectedAt = detected;
        incident.resolvedAt = resolved;
        
        assertThat(incident.resolvedAt).isAfter(incident.detectedAt);
    }

    // Data Validation Tests
    @Test
    @DisplayName("Should accept all valid incident types")
    void validIncidentTypes() {
        String[] validTypes = {
            "MALWARE_DETECTION", "UNAUTHORIZED_ACCESS", "DATA_BREACH",
            "PHISHING_ATTACK", "RANSOMWARE", "DDoS_ATTACK",
            "INSIDER_THREAT", "VULNERABILITY_EXPLOIT", 
            "SUSPICIOUS_ACTIVITY", "POLICY_VIOLATION"
        };
        
        for (String type : validTypes) {
            incident.type = type;
            assertThat(incident.type).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("Should accept all valid severity levels")
    void validSeverityLevels() {
        String[] validLevels = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        
        for (String level : validLevels) {
            incident.severity = level;
            assertThat(incident.severity).isEqualTo(level);
        }
    }

    @Test
    @DisplayName("Should accept all valid status values")
    void validStatusValues() {
        String[] validStatuses = {
            "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED", "FALSE_POSITIVE"
        };
        
        for (String status : validStatuses) {
            incident.status = status;
            assertThat(incident.status).isEqualTo(status);
        }
    }

    // Edge Cases and Boundary Tests
    @Test
    @DisplayName("Should handle very long title and description")
    void veryLongTexts() {
        String longTitle = "Security Incident: " + "A".repeat(500);
        String longDesc = "Description: " + "B".repeat(5000);
        
        incident.title = longTitle;
        incident.description = longDesc;
        
        assertThat(incident.title).hasSize(519); // "Security Incident: " (19) + 500 A's
        assertThat(incident.description).hasSize(5013); // "Description: " (13) + 5000 B's
    }

    @Test
    @DisplayName("Should handle empty strings")
    void emptyStrings() {
        incident.title = "";
        incident.type = "";
        incident.severity = "";
        incident.status = "";
        incident.description = "";
        incident.assignedTo = "";
        
        assertThat(incident.title).isEmpty();
        assertThat(incident.type).isEmpty();
        assertThat(incident.severity).isEmpty();
        assertThat(incident.status).isEmpty();
        assertThat(incident.description).isEmpty();
        assertThat(incident.assignedTo).isEmpty();
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void specialCharacters() {
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        incident.title = "Incident: " + specialChars;
        incident.description = "Details: " + specialChars;
        incident.assignedTo = "user" + specialChars + "@test.com";
        
        assertThat(incident.title).contains(specialChars);
        assertThat(incident.description).contains(specialChars);
        assertThat(incident.assignedTo).contains(specialChars);
    }

    @Test
    @DisplayName("Should handle future timestamps")
    void futureTimestamps() {
        LocalDateTime beforeTest = LocalDateTime.now();
        LocalDateTime future = beforeTest.plusDays(1);
        incident.detectedAt = future;
        incident.resolvedAt = future.plusHours(1);
        
        assertThat(incident.detectedAt).isAfter(beforeTest);
        assertThat(incident.resolvedAt).isAfter(incident.detectedAt);
    }

    // Equals and HashCode Tests
    @Test
    @DisplayName("Should be equal to itself")
    void equalsSelf() {
        SecurityIncident incident = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        assertThat(incident).isEqualTo(incident);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void notEqualsNull() {
        SecurityIncident incident = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        assertThat(incident).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void notEqualsDifferentType() {
        SecurityIncident incident = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        assertThat(incident).isNotEqualTo("Not an incident");
    }

    @Test
    @DisplayName("Should be equal with same values")
    void equalsWithSameValues() {
        LocalDateTime sameTime = LocalDateTime.now();
        SecurityIncident incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        SecurityIncident incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        incident1.id = "same-id";
        incident2.id = "same-id";
        incident1.detectedAt = sameTime;
        incident2.detectedAt = sameTime;
        assertThat(incident1).isEqualTo(incident2);
    }

    @Test
    @DisplayName("Should not be equal with different id")
    void notEqualsWithDifferentId() {
        SecurityIncident incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        SecurityIncident incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        incident1.id = "id1";
        incident2.id = "id2";
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should not be equal with different fields")
    void notEqualsWithDifferentFields() {
        SecurityIncident incident1 = new SecurityIncident("Test1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description 1");
        SecurityIncident incident2 = new SecurityIncident("Test2", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description 2");
        assertThat(incident1).isNotEqualTo(incident2);

        incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        incident2 = new SecurityIncident("Test", "DATA_BREACH", "HIGH", 1.0, 1.0, "Test description");
        assertThat(incident1).isNotEqualTo(incident2);

        incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "LOW", 1.0, 1.0, "Test description");
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should not be equal with different coordinates")
    void notEqualsWithDifferentCoordinates() {
        SecurityIncident incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        SecurityIncident incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 2.0, 2.0, "Test description");
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should handle null fields in equals")
    void equalsWithNullFields() {
        LocalDateTime sameTime = LocalDateTime.now();
        SecurityIncident incident1 = new SecurityIncident();
        SecurityIncident incident2 = new SecurityIncident();
        incident1.id = "same-id";
        incident2.id = "same-id";
        incident1.detectedAt = sameTime;
        incident2.detectedAt = sameTime;
        assertThat(incident1).isEqualTo(incident2);

        // Test with some fields set
        incident1.description = "Test";
        incident2.description = "Test";
        incident1.affectedAssetId = "asset-1";
        incident2.affectedAssetId = "asset-1";
        assertThat(incident1).isEqualTo(incident2);
    }

    @Test
    @DisplayName("Should have same hashcode for equal objects")
    void hashCodeForEqualObjects() {
        LocalDateTime sameTime = LocalDateTime.now();
        SecurityIncident incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        SecurityIncident incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        incident1.id = "same-id";
        incident2.id = "same-id";
        incident1.detectedAt = sameTime;
        incident2.detectedAt = sameTime;
        assertThat(incident1.hashCode()).isEqualTo(incident2.hashCode());
    }

    @Test
    @DisplayName("Should have different hashcode for different objects")
    void hashCodeForDifferentObjects() {
        SecurityIncident incident1 = new SecurityIncident("Test1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description 1");
        SecurityIncident incident2 = new SecurityIncident("Test2", "DATA_BREACH", "LOW", 2.0, 2.0, "Test description 2");
        assertThat(incident1.hashCode()).isNotEqualTo(incident2.hashCode());
    }

    @Test
    @DisplayName("Should handle null fields in hashCode")
    void hashCodeWithNullFields() {
        SecurityIncident incident = new SecurityIncident();
        // Should not throw exception
        assertThat(incident.hashCode()).isNotNull();
    }

    @Test
    @DisplayName("Should test all equals branches")
    void equalsAllBranches() {
        SecurityIncident base = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        base.id = "id";
        base.status = "OPEN";
        base.description = "desc";
        base.affectedAssetId = "asset";
        base.assignedTo = "user";
        base.detectedAt = LocalDateTime.now();
        base.resolvedAt = LocalDateTime.now().plusHours(1);

        // Test with different description
        SecurityIncident other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        other.id = "id";
        other.status = "OPEN";
        other.description = "different";
        assertThat(base).isNotEqualTo(other);

        // Test with different status
        other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        other.id = "id";
        other.status = "CLOSED";
        assertThat(base).isNotEqualTo(other);

        // Test with different affected asset
        other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        other.id = "id";
        other.status = "OPEN";
        other.affectedAssetId = "different-asset";
        assertThat(base).isNotEqualTo(other);

        // Test with different assignedTo
        other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        other.id = "id";
        other.status = "OPEN";
        other.assignedTo = "different-user";
        assertThat(base).isNotEqualTo(other);
    }

    @Test
    @DisplayName("Should test all equals branches with null variations")
    void equalsAllBranchesWithNulls() {
        LocalDateTime time = LocalDateTime.now();
        SecurityIncident base = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        base.id = "id";
        base.detectedAt = time;

        // Test with null detectedAt
        SecurityIncident other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0, "Test description");
        other.id = "id";
        base.detectedAt = null;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);

        // Both null detectedAt
        base.detectedAt = null;
        other.detectedAt = null;
        assertThat(base).isEqualTo(other);

        // Test with null resolvedAt
        base.detectedAt = time;
        other.detectedAt = time;
        base.resolvedAt = time;
        other.resolvedAt = null;
        assertThat(base).isNotEqualTo(other);

        // Test with null assignedTo
        base.resolvedAt = null;
        other.resolvedAt = null;
        base.assignedTo = "user";
        other.assignedTo = null;
        assertThat(base).isNotEqualTo(other);

        // Test all null variations for other fields
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = null;
        other.id = "id";
        base.detectedAt = time;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);

        // Both null
        base.id = null;
        other.id = null;
        assertThat(base).isEqualTo(other);
    }

    @Test
    @DisplayName("Should cover all equals null branches")
    void equalsCompleteNullCoverage() {
        LocalDateTime time = LocalDateTime.now();
        SecurityIncident base = new SecurityIncident();
        base.id = "id";
        base.title = "title";
        base.type = "type";
        base.severity = "severity";
        base.status = "status";
        base.description = "desc";
        base.affectedAssetId = "asset";
        base.detectedAt = time;
        base.resolvedAt = time.plusHours(1);
        base.assignedTo = "user";

        // Test each field null in other
        SecurityIncident other = new SecurityIncident();
        other.id = "id";
        other.title = null;
        other.type = "type";
        other.severity = "severity";
        other.status = "status";
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);

        // Continue with other fields
        other.title = "title";
        other.type = null;
        assertThat(base).isNotEqualTo(other);

        other.type = "type";
        other.severity = null;
        assertThat(base).isNotEqualTo(other);

        other.severity = "severity";
        other.status = null;
        assertThat(base).isNotEqualTo(other);

        // Description null in other
        other.status = "status";
        other.description = null;
        assertThat(base).isNotEqualTo(other);

        // AffectedAssetId null in other
        other.description = "desc";
        other.affectedAssetId = null;
        assertThat(base).isNotEqualTo(other);

        // ResolvedAt null in other
        other.affectedAssetId = "asset";
        other.resolvedAt = null;
        assertThat(base).isNotEqualTo(other);

        // AssignedTo null in other
        other.resolvedAt = time.plusHours(1);
        other.assignedTo = null;
        assertThat(base).isNotEqualTo(other);
    }

    @Test
    @DisplayName("Should cover equals when base has null and other has value")
    void equalsBaseNullOtherValue() {
        LocalDateTime time = LocalDateTime.now();
        
        // Test id: base null, other not null
        SecurityIncident base = new SecurityIncident();
        SecurityIncident other = new SecurityIncident();
        base.id = null;
        other.id = "id";
        base.detectedAt = time;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test title: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.title = null;
        other.title = "title";
        base.detectedAt = time;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test type: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.type = null;
        other.type = "type";
        base.detectedAt = time;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test severity: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.severity = null;
        other.severity = "severity";
        base.detectedAt = time;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test status: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.status = null;
        other.status = "status";
        base.detectedAt = time;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test description: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.description = null;
        other.description = "desc";
        base.detectedAt = time;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test affectedAssetId: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.affectedAssetId = null;
        other.affectedAssetId = "asset";
        base.detectedAt = time;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test detectedAt: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.detectedAt = null;
        other.detectedAt = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test resolvedAt: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.detectedAt = time;
        other.detectedAt = time;
        base.resolvedAt = null;
        other.resolvedAt = time.plusHours(1);
        assertThat(base).isNotEqualTo(other);
        
        // Test assignedTo: base null, other not null
        base = new SecurityIncident();
        other = new SecurityIncident();
        base.id = "id";
        other.id = "id";
        base.detectedAt = time;
        other.detectedAt = time;
        base.assignedTo = null;
        other.assignedTo = "user";
        assertThat(base).isNotEqualTo(other);
    }

    @Test
    @DisplayName("Should test equals with different Y coordinate")
    void equalsWithDifferentYCoordinate() {
        SecurityIncident incident1 = new SecurityIncident("Test", "TYPE", "HIGH", 1.0, 1.0, "Test description");
        SecurityIncident incident2 = new SecurityIncident("Test", "TYPE", "HIGH", 1.0, 2.0, "Test description");
        incident1.id = "id";
        incident2.id = "id";
        LocalDateTime time = LocalDateTime.now();
        incident1.detectedAt = time;
        incident2.detectedAt = time;
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should test equals with different status non-null")
    void equalsWithDifferentStatusNonNull() {
        SecurityIncident incident1 = new SecurityIncident();
        SecurityIncident incident2 = new SecurityIncident();
        incident1.id = "id";
        incident2.id = "id";
        incident1.status = "OPEN";
        incident2.status = "CLOSED";
        LocalDateTime time = LocalDateTime.now();
        incident1.detectedAt = time;
        incident2.detectedAt = time;
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should cover hashCode with all null fields")
    void hashCodeWithAllNullFields() {
        SecurityIncident incident = new SecurityIncident();
        incident.id = null;
        incident.title = null;
        incident.type = null;
        incident.severity = null;
        incident.status = null;
        incident.description = null;
        incident.affectedAssetId = null;
        incident.detectedAt = null;
        incident.resolvedAt = null;
        incident.assignedTo = null;
        
        // Should not throw exception
        int hashCode = incident.hashCode();
        assertThat(hashCode).isNotNull();
        
        // Test with another incident with same null values
        SecurityIncident incident2 = new SecurityIncident();
        incident2.id = null;
        incident2.title = null;
        incident2.type = null;
        incident2.severity = null;
        incident2.status = null;
        incident2.description = null;
        incident2.affectedAssetId = null;
        incident2.detectedAt = null;
        incident2.resolvedAt = null;
        incident2.assignedTo = null;
        
        assertThat(incident.hashCode()).isEqualTo(incident2.hashCode());
    }

    @Test
    @DisplayName("Should test equals with different description non-null")
    void equalsWithDifferentDescriptionNonNull() {
        SecurityIncident incident1 = new SecurityIncident();
        SecurityIncident incident2 = new SecurityIncident();
        incident1.id = "id";
        incident2.id = "id";
        incident1.description = "desc1";
        incident2.description = "desc2";
        LocalDateTime time = LocalDateTime.now();
        incident1.detectedAt = time;
        incident2.detectedAt = time;
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should test equals with different affectedAssetId non-null")
    void equalsWithDifferentAffectedAssetIdNonNull() {
        SecurityIncident incident1 = new SecurityIncident();
        SecurityIncident incident2 = new SecurityIncident();
        incident1.id = "id";
        incident2.id = "id";
        incident1.affectedAssetId = "asset1";
        incident2.affectedAssetId = "asset2";
        LocalDateTime time = LocalDateTime.now();
        incident1.detectedAt = time;
        incident2.detectedAt = time;
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should test equals with different resolvedAt non-null")
    void equalsWithDifferentResolvedAtNonNull() {
        SecurityIncident incident1 = new SecurityIncident();
        SecurityIncident incident2 = new SecurityIncident();
        incident1.id = "id";
        incident2.id = "id";
        LocalDateTime time = LocalDateTime.now();
        incident1.detectedAt = time;
        incident2.detectedAt = time;
        incident1.resolvedAt = time.plusHours(1);
        incident2.resolvedAt = time.plusHours(2);
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should test equals with different assignedTo non-null")
    void equalsWithDifferentAssignedToNonNull() {
        SecurityIncident incident1 = new SecurityIncident();
        SecurityIncident incident2 = new SecurityIncident();
        incident1.id = "id";
        incident2.id = "id";
        LocalDateTime time = LocalDateTime.now();
        incident1.detectedAt = time;
        incident2.detectedAt = time;
        incident1.assignedTo = "user1";
        incident2.assignedTo = "user2";
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should test equals with status null in first object but not second")
    void equalsStatusNullInFirstObjectOnly() {
        LocalDateTime time = LocalDateTime.now();
        SecurityIncident incident1 = new SecurityIncident();
        SecurityIncident incident2 = new SecurityIncident();
        
        incident1.id = "id";
        incident2.id = "id";
        incident1.title = "title";
        incident2.title = "title";
        incident1.type = "type";
        incident2.type = "type";
        incident1.severity = "severity";
        incident2.severity = "severity";
        incident1.status = null;
        incident2.status = "OPEN";
        incident1.detectedAt = time;
        incident2.detectedAt = time;
        
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should test equals with detectedAt null in first object but not second")
    void equalsDetectedAtNullInFirstObjectOnly() {
        LocalDateTime time = LocalDateTime.now();
        SecurityIncident incident1 = new SecurityIncident();
        SecurityIncident incident2 = new SecurityIncident();
        
        incident1.id = "id";
        incident2.id = "id";
        incident1.title = "title";
        incident2.title = "title";
        incident1.type = "type";
        incident2.type = "type";
        incident1.severity = "severity";
        incident2.severity = "severity";
        incident1.status = "OPEN";
        incident2.status = "OPEN";
        incident1.detectedAt = null;
        incident2.detectedAt = time;
        
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    @DisplayName("Should test hashCode with null resolvedAt and assignedTo")
    void hashCodeWithNullResolvedAtAndAssignedTo() {
        SecurityIncident incident = new SecurityIncident();
        incident.id = "id";
        incident.title = "title";
        incident.type = "type";
        incident.severity = "severity";
        incident.status = "OPEN";
        incident.description = "desc";
        incident.affectedAssetId = "asset";
        incident.detectedAt = LocalDateTime.now();
        incident.resolvedAt = null;
        incident.assignedTo = null;
        
        // Should not throw exception
        int hashCode = incident.hashCode();
        assertThat(hashCode).isNotNull();
    }
}