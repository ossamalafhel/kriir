package com.kriir.platform.incident;

import com.kriir.platform.model.SecurityIncident;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SecurityIncident Model Tests")
class SecurityIncidentTest {

    private SecurityIncident incident;

    @BeforeEach
    void setUp() {
        incident = new SecurityIncident();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should generate UUID and set defaults")
        void defaultConstructor() {
            SecurityIncident newIncident = new SecurityIncident();
            
            assertThat(newIncident.getId()).isNotNull();
            assertThat(newIncident.getId()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
            assertThat(newIncident.getStatus()).isEqualTo("OPEN");
            assertThat(newIncident.getDetectedAt()).isNotNull();
            assertThat(newIncident.getDetectedAt()).isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(newIncident.getResolvedAt()).isNull();
        }

        @Test
        @DisplayName("Parameterized constructor should set all fields correctly")
        void parameterizedConstructor() {
            SecurityIncident newIncident = new SecurityIncident(
                "Malware Detected", 
                "MALWARE_DETECTION", 
                "HIGH", 
                7.06064, 
                48.092971
            );
            
            assertThat(newIncident.getId()).isNotNull();
            assertThat(newIncident.getTitle()).isEqualTo("Malware Detected");
            assertThat(newIncident.getType()).isEqualTo("MALWARE_DETECTION");
            assertThat(newIncident.getSeverity()).isEqualTo("HIGH");
            assertThat(newIncident.getX()).isEqualTo(7.06064);
            assertThat(newIncident.getY()).isEqualTo(48.092971);
            assertThat(newIncident.getStatus()).isEqualTo("OPEN");
            assertThat(newIncident.getDetectedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get ID correctly")
        void idGetterSetter() {
            String id = UUID.randomUUID().toString();
            incident.setId(id);
            assertThat(incident.getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("Should set and get title correctly")
        void titleGetterSetter() {
            incident.setTitle("DDoS Attack Detected");
            assertThat(incident.getTitle()).isEqualTo("DDoS Attack Detected");
        }

        @Test
        @DisplayName("Should set and get type correctly")
        void typeGetterSetter() {
            incident.setType("DDoS_ATTACK");
            assertThat(incident.getType()).isEqualTo("DDoS_ATTACK");
        }

        @Test
        @DisplayName("Should set and get severity correctly")
        void severityGetterSetter() {
            incident.setSeverity("CRITICAL");
            assertThat(incident.getSeverity()).isEqualTo("CRITICAL");
        }

        @Test
        @DisplayName("Should set and get status correctly")
        void statusGetterSetter() {
            incident.setStatus("IN_PROGRESS");
            assertThat(incident.getStatus()).isEqualTo("IN_PROGRESS");
        }

        @Test
        @DisplayName("Should set and get coordinates correctly")
        void coordinatesGetterSetter() {
            incident.setX(2.3522);
            incident.setY(48.8566);
            assertThat(incident.getX()).isEqualTo(2.3522);
            assertThat(incident.getY()).isEqualTo(48.8566);
        }

        @Test
        @DisplayName("Should set and get description correctly")
        void descriptionGetterSetter() {
            String desc = "Multiple failed login attempts detected from unknown IP addresses";
            incident.setDescription(desc);
            assertThat(incident.getDescription()).isEqualTo(desc);
        }

        @Test
        @DisplayName("Should set and get affectedAssetId correctly")
        void affectedAssetIdGetterSetter() {
            String assetId = UUID.randomUUID().toString();
            incident.setAffectedAssetId(assetId);
            assertThat(incident.getAffectedAssetId()).isEqualTo(assetId);
        }

        @Test
        @DisplayName("Should set and get timestamps correctly")
        void timestampsGetterSetter() {
            LocalDateTime detected = LocalDateTime.now().minusHours(2);
            LocalDateTime resolved = LocalDateTime.now();
            
            incident.setDetectedAt(detected);
            incident.setResolvedAt(resolved);
            
            assertThat(incident.getDetectedAt()).isEqualTo(detected);
            assertThat(incident.getResolvedAt()).isEqualTo(resolved);
        }

        @Test
        @DisplayName("Should set and get assignedTo correctly")
        void assignedToGetterSetter() {
            incident.setAssignedTo("john.doe@company.com");
            assertThat(incident.getAssignedTo()).isEqualTo("john.doe@company.com");
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should handle null values appropriately")
        void handleNullValues() {
            incident.setTitle(null);
            incident.setType(null);
            incident.setSeverity(null);
            incident.setDescription(null);
            incident.setAffectedAssetId(null);
            incident.setAssignedTo(null);
            
            assertThat(incident.getTitle()).isNull();
            assertThat(incident.getType()).isNull();
            assertThat(incident.getSeverity()).isNull();
            assertThat(incident.getDescription()).isNull();
            assertThat(incident.getAffectedAssetId()).isNull();
            assertThat(incident.getAssignedTo()).isNull();
            
            // But status and ID should not be null
            assertThat(incident.getStatus()).isNotNull();
            assertThat(incident.getId()).isNotNull();
            assertThat(incident.getDetectedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should handle incident lifecycle correctly")
        void incidentLifecycle() {
            // Initial state
            assertThat(incident.getStatus()).isEqualTo("OPEN");
            assertThat(incident.getResolvedAt()).isNull();
            
            // In progress
            incident.setStatus("IN_PROGRESS");
            incident.setAssignedTo("analyst@company.com");
            assertThat(incident.getStatus()).isEqualTo("IN_PROGRESS");
            assertThat(incident.getAssignedTo()).isEqualTo("analyst@company.com");
            
            // Resolved
            LocalDateTime resolvedTime = LocalDateTime.now();
            incident.setStatus("RESOLVED");
            incident.setResolvedAt(resolvedTime);
            assertThat(incident.getStatus()).isEqualTo("RESOLVED");
            assertThat(incident.getResolvedAt()).isEqualTo(resolvedTime);
        }

        @Test
        @DisplayName("Should calculate incident duration correctly")
        void incidentDuration() {
            LocalDateTime detected = LocalDateTime.now().minusHours(3);
            LocalDateTime resolved = LocalDateTime.now();
            
            incident.setDetectedAt(detected);
            incident.setResolvedAt(resolved);
            
            assertThat(incident.getResolvedAt()).isAfter(incident.getDetectedAt());
        }
    }

    @Nested
    @DisplayName("Data Validation Tests")
    class DataValidationTests {

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
                incident.setType(type);
                assertThat(incident.getType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("Should accept all valid severity levels")
        void validSeverityLevels() {
            String[] validLevels = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
            
            for (String level : validLevels) {
                incident.setSeverity(level);
                assertThat(incident.getSeverity()).isEqualTo(level);
            }
        }

        @Test
        @DisplayName("Should accept all valid status values")
        void validStatusValues() {
            String[] validStatuses = {
                "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED", "FALSE_POSITIVE"
            };
            
            for (String status : validStatuses) {
                incident.setStatus(status);
                assertThat(incident.getStatus()).isEqualTo(status);
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long title and description")
        void veryLongTexts() {
            String longTitle = "Security Incident: " + "A".repeat(500);
            String longDesc = "Description: " + "B".repeat(5000);
            
            incident.setTitle(longTitle);
            incident.setDescription(longDesc);
            
            assertThat(incident.getTitle()).hasSize(519); // "Security Incident: " (19) + 500 A's
            assertThat(incident.getDescription()).hasSize(5013); // "Description: " (13) + 5000 B's
        }

        @Test
        @DisplayName("Should handle empty strings")
        void emptyStrings() {
            incident.setTitle("");
            incident.setType("");
            incident.setSeverity("");
            incident.setStatus("");
            incident.setDescription("");
            incident.setAssignedTo("");
            
            assertThat(incident.getTitle()).isEmpty();
            assertThat(incident.getType()).isEmpty();
            assertThat(incident.getSeverity()).isEmpty();
            assertThat(incident.getStatus()).isEmpty();
            assertThat(incident.getDescription()).isEmpty();
            assertThat(incident.getAssignedTo()).isEmpty();
        }

        @Test
        @DisplayName("Should handle special characters in text fields")
        void specialCharacters() {
            String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
            incident.setTitle("Incident: " + specialChars);
            incident.setDescription("Details: " + specialChars);
            incident.setAssignedTo("user" + specialChars + "@test.com");
            
            assertThat(incident.getTitle()).contains(specialChars);
            assertThat(incident.getDescription()).contains(specialChars);
            assertThat(incident.getAssignedTo()).contains(specialChars);
        }

        @Test
        @DisplayName("Should handle future timestamps")
        void futureTimestamps() {
            LocalDateTime beforeTest = LocalDateTime.now();
            LocalDateTime future = beforeTest.plusDays(1);
            incident.setDetectedAt(future);
            incident.setResolvedAt(future.plusHours(1));
            
            assertThat(incident.getDetectedAt()).isAfter(beforeTest);
            assertThat(incident.getResolvedAt()).isAfter(incident.getDetectedAt());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void equalsSelf() {
            SecurityIncident incident = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            assertThat(incident).isEqualTo(incident);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void notEqualsNull() {
            SecurityIncident incident = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            assertThat(incident).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void notEqualsDifferentType() {
            SecurityIncident incident = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            assertThat(incident).isNotEqualTo("Not an incident");
        }

        @Test
        @DisplayName("Should be equal with same values")
        void equalsWithSameValues() {
            LocalDateTime sameTime = LocalDateTime.now();
            SecurityIncident incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            incident1.setId("same-id");
            incident2.setId("same-id");
            incident1.setDetectedAt(sameTime);
            incident2.setDetectedAt(sameTime);
            assertThat(incident1).isEqualTo(incident2);
        }

        @Test
        @DisplayName("Should not be equal with different id")
        void notEqualsWithDifferentId() {
            SecurityIncident incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            incident1.setId("id1");
            incident2.setId("id2");
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should not be equal with different fields")
        void notEqualsWithDifferentFields() {
            SecurityIncident incident1 = new SecurityIncident("Test1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Test2", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            assertThat(incident1).isNotEqualTo(incident2);

            incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            incident2 = new SecurityIncident("Test", "DATA_BREACH", "HIGH", 1.0, 1.0);
            assertThat(incident1).isNotEqualTo(incident2);

            incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "LOW", 1.0, 1.0);
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should not be equal with different coordinates")
        void notEqualsWithDifferentCoordinates() {
            SecurityIncident incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 2.0, 2.0);
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should handle null fields in equals")
        void equalsWithNullFields() {
            LocalDateTime sameTime = LocalDateTime.now();
            SecurityIncident incident1 = new SecurityIncident();
            SecurityIncident incident2 = new SecurityIncident();
            incident1.setId("same-id");
            incident2.setId("same-id");
            incident1.setDetectedAt(sameTime);
            incident2.setDetectedAt(sameTime);
            assertThat(incident1).isEqualTo(incident2);

            // Test with some fields set
            incident1.setDescription("Test");
            incident2.setDescription("Test");
            incident1.setAffectedAssetId("asset-1");
            incident2.setAffectedAssetId("asset-1");
            assertThat(incident1).isEqualTo(incident2);
        }

        @Test
        @DisplayName("Should have same hashcode for equal objects")
        void hashCodeForEqualObjects() {
            LocalDateTime sameTime = LocalDateTime.now();
            SecurityIncident incident1 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            incident1.setId("same-id");
            incident2.setId("same-id");
            incident1.setDetectedAt(sameTime);
            incident2.setDetectedAt(sameTime);
            assertThat(incident1.hashCode()).isEqualTo(incident2.hashCode());
        }

        @Test
        @DisplayName("Should have different hashcode for different objects")
        void hashCodeForDifferentObjects() {
            SecurityIncident incident1 = new SecurityIncident("Test1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Test2", "DATA_BREACH", "LOW", 2.0, 2.0);
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
            SecurityIncident base = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            base.setId("id");
            base.setStatus("OPEN");
            base.setDescription("desc");
            base.setAffectedAssetId("asset");
            base.setAssignedTo("user");
            base.setDetectedAt(LocalDateTime.now());
            base.setResolvedAt(LocalDateTime.now().plusHours(1));

            // Test with different description
            SecurityIncident other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            other.setId("id");
            other.setStatus("OPEN");
            other.setDescription("different");
            assertThat(base).isNotEqualTo(other);

            // Test with different status
            other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            other.setId("id");
            other.setStatus("CLOSED");
            assertThat(base).isNotEqualTo(other);

            // Test with different affected asset
            other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            other.setId("id");
            other.setStatus("OPEN");
            other.setAffectedAssetId("different-asset");
            assertThat(base).isNotEqualTo(other);

            // Test with different assignedTo
            other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            other.setId("id");
            other.setStatus("OPEN");
            other.setAssignedTo("different-user");
            assertThat(base).isNotEqualTo(other);
        }

        @Test
        @DisplayName("Should test all equals branches with null variations")
        void equalsAllBranchesWithNulls() {
            LocalDateTime time = LocalDateTime.now();
            SecurityIncident base = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            base.setId("id");
            base.setDetectedAt(time);

            // Test with null detectedAt
            SecurityIncident other = new SecurityIncident("Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            other.setId("id");
            base.setDetectedAt(null);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);

            // Both null detectedAt
            base.setDetectedAt(null);
            other.setDetectedAt(null);
            assertThat(base).isEqualTo(other);

            // Test with null resolvedAt
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            base.setResolvedAt(time);
            other.setResolvedAt(null);
            assertThat(base).isNotEqualTo(other);

            // Test with null assignedTo
            base.setResolvedAt(null);
            other.setResolvedAt(null);
            base.setAssignedTo("user");
            other.setAssignedTo(null);
            assertThat(base).isNotEqualTo(other);

            // Test all null variations for other fields
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId(null);
            other.setId("id");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);

            // Both null
            base.setId(null);
            other.setId(null);
            assertThat(base).isEqualTo(other);
        }

        @Test
        @DisplayName("Should cover all equals null branches")
        void equalsCompleteNullCoverage() {
            LocalDateTime time = LocalDateTime.now();
            SecurityIncident base = new SecurityIncident();
            base.setId("id");
            base.setTitle("title");
            base.setType("type");
            base.setSeverity("severity");
            base.setStatus("status");
            base.setDescription("desc");
            base.setAffectedAssetId("asset");
            base.setDetectedAt(time);
            base.setResolvedAt(time.plusHours(1));
            base.setAssignedTo("user");

            // Test each field null in other
            SecurityIncident other = new SecurityIncident();
            other.setId("id");
            other.setTitle(null);
            other.setType("type");
            other.setSeverity("severity");
            other.setStatus("status");
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);

            // Continue with other fields
            other.setTitle("title");
            other.setType(null);
            assertThat(base).isNotEqualTo(other);

            other.setType("type");
            other.setSeverity(null);
            assertThat(base).isNotEqualTo(other);

            other.setSeverity("severity");
            other.setStatus(null);
            assertThat(base).isNotEqualTo(other);

            // Description null in other
            other.setStatus("status");
            other.setDescription(null);
            assertThat(base).isNotEqualTo(other);

            // AffectedAssetId null in other
            other.setDescription("desc");
            other.setAffectedAssetId(null);
            assertThat(base).isNotEqualTo(other);

            // ResolvedAt null in other
            other.setAffectedAssetId("asset");
            other.setResolvedAt(null);
            assertThat(base).isNotEqualTo(other);

            // AssignedTo null in other
            other.setResolvedAt(time.plusHours(1));
            other.setAssignedTo(null);
            assertThat(base).isNotEqualTo(other);
        }

        @Test
        @DisplayName("Should cover equals when base has null and other has value")
        void equalsBaseNullOtherValue() {
            LocalDateTime time = LocalDateTime.now();
            
            // Test id: base null, other not null
            SecurityIncident base = new SecurityIncident();
            SecurityIncident other = new SecurityIncident();
            base.setId(null);
            other.setId("id");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test title: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setTitle(null);
            other.setTitle("title");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test type: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setType(null);
            other.setType("type");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test severity: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setSeverity(null);
            other.setSeverity("severity");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test status: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setStatus(null);
            other.setStatus("status");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test description: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setDescription(null);
            other.setDescription("desc");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test affectedAssetId: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setAffectedAssetId(null);
            other.setAffectedAssetId("asset");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test detectedAt: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setDetectedAt(null);
            other.setDetectedAt(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test resolvedAt: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            base.setResolvedAt(null);
            other.setResolvedAt(time.plusHours(1));
            assertThat(base).isNotEqualTo(other);
            
            // Test assignedTo: base null, other not null
            base = new SecurityIncident();
            other = new SecurityIncident();
            base.setId("id");
            other.setId("id");
            base.setDetectedAt(time);
            other.setDetectedAt(time);
            base.setAssignedTo(null);
            other.setAssignedTo("user");
            assertThat(base).isNotEqualTo(other);
        }

        @Test
        @DisplayName("Should test equals with different Y coordinate")
        void equalsWithDifferentYCoordinate() {
            SecurityIncident incident1 = new SecurityIncident("Test", "TYPE", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Test", "TYPE", "HIGH", 1.0, 2.0);
            incident1.setId("id");
            incident2.setId("id");
            LocalDateTime time = LocalDateTime.now();
            incident1.setDetectedAt(time);
            incident2.setDetectedAt(time);
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should test equals with different status non-null")
        void equalsWithDifferentStatusNonNull() {
            SecurityIncident incident1 = new SecurityIncident();
            SecurityIncident incident2 = new SecurityIncident();
            incident1.setId("id");
            incident2.setId("id");
            incident1.setStatus("OPEN");
            incident2.setStatus("CLOSED");
            LocalDateTime time = LocalDateTime.now();
            incident1.setDetectedAt(time);
            incident2.setDetectedAt(time);
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should cover hashCode with all null fields")
        void hashCodeWithAllNullFields() {
            SecurityIncident incident = new SecurityIncident();
            incident.setId(null);
            incident.setTitle(null);
            incident.setType(null);
            incident.setSeverity(null);
            incident.setStatus(null);
            incident.setDescription(null);
            incident.setAffectedAssetId(null);
            incident.setDetectedAt(null);
            incident.setResolvedAt(null);
            incident.setAssignedTo(null);
            
            // Should not throw exception
            int hashCode = incident.hashCode();
            assertThat(hashCode).isNotNull();
            
            // Test with another incident with same null values
            SecurityIncident incident2 = new SecurityIncident();
            incident2.setId(null);
            incident2.setTitle(null);
            incident2.setType(null);
            incident2.setSeverity(null);
            incident2.setStatus(null);
            incident2.setDescription(null);
            incident2.setAffectedAssetId(null);
            incident2.setDetectedAt(null);
            incident2.setResolvedAt(null);
            incident2.setAssignedTo(null);
            
            assertThat(incident.hashCode()).isEqualTo(incident2.hashCode());
        }

        @Test
        @DisplayName("Should test equals with different description non-null")
        void equalsWithDifferentDescriptionNonNull() {
            SecurityIncident incident1 = new SecurityIncident();
            SecurityIncident incident2 = new SecurityIncident();
            incident1.setId("id");
            incident2.setId("id");
            incident1.setDescription("desc1");
            incident2.setDescription("desc2");
            LocalDateTime time = LocalDateTime.now();
            incident1.setDetectedAt(time);
            incident2.setDetectedAt(time);
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should test equals with different affectedAssetId non-null")
        void equalsWithDifferentAffectedAssetIdNonNull() {
            SecurityIncident incident1 = new SecurityIncident();
            SecurityIncident incident2 = new SecurityIncident();
            incident1.setId("id");
            incident2.setId("id");
            incident1.setAffectedAssetId("asset1");
            incident2.setAffectedAssetId("asset2");
            LocalDateTime time = LocalDateTime.now();
            incident1.setDetectedAt(time);
            incident2.setDetectedAt(time);
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should test equals with different resolvedAt non-null")
        void equalsWithDifferentResolvedAtNonNull() {
            SecurityIncident incident1 = new SecurityIncident();
            SecurityIncident incident2 = new SecurityIncident();
            incident1.setId("id");
            incident2.setId("id");
            LocalDateTime time = LocalDateTime.now();
            incident1.setDetectedAt(time);
            incident2.setDetectedAt(time);
            incident1.setResolvedAt(time.plusHours(1));
            incident2.setResolvedAt(time.plusHours(2));
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should test equals with different assignedTo non-null")
        void equalsWithDifferentAssignedToNonNull() {
            SecurityIncident incident1 = new SecurityIncident();
            SecurityIncident incident2 = new SecurityIncident();
            incident1.setId("id");
            incident2.setId("id");
            LocalDateTime time = LocalDateTime.now();
            incident1.setDetectedAt(time);
            incident2.setDetectedAt(time);
            incident1.setAssignedTo("user1");
            incident2.setAssignedTo("user2");
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should test equals with status null in first object but not second")
        void equalsStatusNullInFirstObjectOnly() {
            LocalDateTime time = LocalDateTime.now();
            SecurityIncident incident1 = new SecurityIncident();
            SecurityIncident incident2 = new SecurityIncident();
            
            incident1.setId("id");
            incident2.setId("id");
            incident1.setTitle("title");
            incident2.setTitle("title");
            incident1.setType("type");
            incident2.setType("type");
            incident1.setSeverity("severity");
            incident2.setSeverity("severity");
            incident1.setStatus(null);
            incident2.setStatus("OPEN");
            incident1.setDetectedAt(time);
            incident2.setDetectedAt(time);
            
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should test equals with detectedAt null in first object but not second")
        void equalsDetectedAtNullInFirstObjectOnly() {
            LocalDateTime time = LocalDateTime.now();
            SecurityIncident incident1 = new SecurityIncident();
            SecurityIncident incident2 = new SecurityIncident();
            
            incident1.setId("id");
            incident2.setId("id");
            incident1.setTitle("title");
            incident2.setTitle("title");
            incident1.setType("type");
            incident2.setType("type");
            incident1.setSeverity("severity");
            incident2.setSeverity("severity");
            incident1.setStatus("OPEN");
            incident2.setStatus("OPEN");
            incident1.setDetectedAt(null);
            incident2.setDetectedAt(time);
            
            assertThat(incident1).isNotEqualTo(incident2);
        }

        @Test
        @DisplayName("Should test hashCode with null resolvedAt and assignedTo")
        void hashCodeWithNullResolvedAtAndAssignedTo() {
            SecurityIncident incident = new SecurityIncident();
            incident.setId("id");
            incident.setTitle("title");
            incident.setType("type");
            incident.setSeverity("severity");
            incident.setStatus("OPEN");
            incident.setDescription("desc");
            incident.setAffectedAssetId("asset");
            incident.setDetectedAt(LocalDateTime.now());
            incident.setResolvedAt(null);
            incident.setAssignedTo(null);
            
            // Should not throw exception
            int hashCode = incident.hashCode();
            assertThat(hashCode).isNotNull();
        }
    }
}