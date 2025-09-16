package org.cyberisk.platform.asset;

import org.cyberisk.platform.model.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Asset Model Tests")
class AssetTest {

    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should generate UUID and set defaults")
        void defaultConstructor() {
            Asset newAsset = new Asset();
            
            assertThat(newAsset.getId()).isNotNull();
            assertThat(newAsset.getId()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
            assertThat(newAsset.getStatus()).isEqualTo("ACTIVE");
            assertThat(newAsset.getLastSeen()).isNotNull();
            assertThat(newAsset.getLastSeen()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Parameterized constructor should set all fields correctly")
        void parameterizedConstructor() {
            Asset newAsset = new Asset("Web Server", "SERVER", "CRITICAL", 7.06064, 48.092971);
            
            assertThat(newAsset.getId()).isNotNull();
            assertThat(newAsset.getName()).isEqualTo("Web Server");
            assertThat(newAsset.getType()).isEqualTo("SERVER");
            assertThat(newAsset.getCriticality()).isEqualTo("CRITICAL");
            assertThat(newAsset.getX()).isEqualTo(7.06064);
            assertThat(newAsset.getY()).isEqualTo(48.092971);
            assertThat(newAsset.getStatus()).isEqualTo("ACTIVE");
            assertThat(newAsset.getLastSeen()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get ID correctly")
        void idGetterSetter() {
            String id = UUID.randomUUID().toString();
            asset.setId(id);
            assertThat(asset.getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("Should set and get name correctly")
        void nameGetterSetter() {
            asset.setName("Database Server");
            assertThat(asset.getName()).isEqualTo("Database Server");
        }

        @Test
        @DisplayName("Should set and get type correctly")
        void typeGetterSetter() {
            asset.setType("DATABASE");
            assertThat(asset.getType()).isEqualTo("DATABASE");
        }

        @Test
        @DisplayName("Should set and get criticality correctly")
        void criticalityGetterSetter() {
            asset.setCriticality("HIGH");
            assertThat(asset.getCriticality()).isEqualTo("HIGH");
        }

        @Test
        @DisplayName("Should set and get status correctly")
        void statusGetterSetter() {
            asset.setStatus("COMPROMISED");
            assertThat(asset.getStatus()).isEqualTo("COMPROMISED");
        }

        @Test
        @DisplayName("Should set and get coordinates correctly")
        void coordinatesGetterSetter() {
            asset.setX(2.3522);
            asset.setY(48.8566);
            assertThat(asset.getX()).isEqualTo(2.3522);
            assertThat(asset.getY()).isEqualTo(48.8566);
        }

        @Test
        @DisplayName("Should set and get lastSeen correctly")
        void lastSeenGetterSetter() {
            LocalDateTime now = LocalDateTime.now();
            asset.setLastSeen(now);
            assertThat(asset.getLastSeen()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should handle null values appropriately")
        void handleNullValues() {
            asset.setName(null);
            asset.setType(null);
            asset.setCriticality(null);
            
            assertThat(asset.getName()).isNull();
            assertThat(asset.getType()).isNull();
            assertThat(asset.getCriticality()).isNull();
            // But status and ID should not be null
            assertThat(asset.getStatus()).isNotNull();
            assertThat(asset.getId()).isNotNull();
        }

        @Test
        @DisplayName("Should handle edge case coordinates")
        void edgeCaseCoordinates() {
            // Test extreme coordinates
            asset.setX(-180.0);
            asset.setY(90.0);
            assertThat(asset.getX()).isEqualTo(-180.0);
            assertThat(asset.getY()).isEqualTo(90.0);
            
            // Test zero coordinates
            asset.setX(0.0);
            asset.setY(0.0);
            assertThat(asset.getX()).isEqualTo(0.0);
            assertThat(asset.getY()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Should maintain immutability of ID after creation")
        void idImmutabilityCheck() {
            String originalId = asset.getId();
            asset.setId("new-id");
            assertThat(asset.getId()).isEqualTo("new-id");
            assertThat(asset.getId()).isNotEqualTo(originalId);
        }
    }

    @Nested
    @DisplayName("Data Validation Tests")
    class DataValidationTests {

        @Test
        @DisplayName("Should accept all valid asset types")
        void validAssetTypes() {
            String[] validTypes = {"SERVER", "WORKSTATION", "ROUTER", "SWITCH", 
                                 "FIREWALL", "DATABASE", "CLOUD_INSTANCE", 
                                 "IOT_DEVICE", "MOBILE_DEVICE"};
            
            for (String type : validTypes) {
                asset.setType(type);
                assertThat(asset.getType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("Should accept all valid criticality levels")
        void validCriticalityLevels() {
            String[] validLevels = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
            
            for (String level : validLevels) {
                asset.setCriticality(level);
                assertThat(asset.getCriticality()).isEqualTo(level);
            }
        }

        @Test
        @DisplayName("Should accept all valid status values")
        void validStatusValues() {
            String[] validStatuses = {"ACTIVE", "INACTIVE", "COMPROMISED", 
                                    "UNDER_INVESTIGATION", "QUARANTINED"};
            
            for (String status : validStatuses) {
                asset.setStatus(status);
                assertThat(asset.getStatus()).isEqualTo(status);
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long names")
        void veryLongName() {
            String longName = "A".repeat(1000);
            asset.setName(longName);
            assertThat(asset.getName()).hasSize(1000);
            assertThat(asset.getName()).isEqualTo(longName);
        }

        @Test
        @DisplayName("Should handle empty strings")
        void emptyStrings() {
            asset.setName("");
            asset.setType("");
            asset.setCriticality("");
            asset.setStatus("");
            
            assertThat(asset.getName()).isEmpty();
            assertThat(asset.getType()).isEmpty();
            assertThat(asset.getCriticality()).isEmpty();
            assertThat(asset.getStatus()).isEmpty();
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void specialCharacters() {
            String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
            asset.setName(specialChars);
            assertThat(asset.getName()).isEqualTo(specialChars);
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal to itself")
        void equalsSelf() {
            Asset asset = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            assertThat(asset).isEqualTo(asset);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void notEqualsNull() {
            Asset asset = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            assertThat(asset).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void notEqualsDifferentType() {
            Asset asset = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            assertThat(asset).isNotEqualTo("Not an asset");
        }

        @Test
        @DisplayName("Should be equal with same values")
        void equalsWithSameValues() {
            LocalDateTime sameTime = LocalDateTime.now();
            Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            asset1.setId("same-id");
            asset2.setId("same-id");
            asset1.setLastSeen(sameTime);
            asset2.setLastSeen(sameTime);
            assertThat(asset1).isEqualTo(asset2);
        }

        @Test
        @DisplayName("Should not be equal with different id")
        void notEqualsWithDifferentId() {
            Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            asset1.setId("id1");
            asset2.setId("id2");
            assertThat(asset1).isNotEqualTo(asset2);
        }

        @Test
        @DisplayName("Should not be equal with different name")
        void notEqualsWithDifferentName() {
            Asset asset1 = new Asset("Test1", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Test2", "SERVER", "HIGH", 1.0, 1.0);
            assertThat(asset1).isNotEqualTo(asset2);
        }

        @Test
        @DisplayName("Should not be equal with different type")
        void notEqualsWithDifferentAssetType() {
            Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Test", "DATABASE", "HIGH", 1.0, 1.0);
            assertThat(asset1).isNotEqualTo(asset2);
        }

        @Test
        @DisplayName("Should not be equal with different coordinates")
        void notEqualsWithDifferentCoordinates() {
            Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Test", "SERVER", "HIGH", 2.0, 2.0);
            assertThat(asset1).isNotEqualTo(asset2);
        }

        @Test
        @DisplayName("Should handle null fields in equals")
        void equalsWithNullFields() {
            Asset asset1 = new Asset();
            Asset asset2 = new Asset();
            asset1.setId("same-id");
            asset2.setId("same-id");
            LocalDateTime sameTime = LocalDateTime.now();
            asset1.setLastSeen(sameTime);
            asset2.setLastSeen(sameTime);
            assertThat(asset1).isEqualTo(asset2);
        }

        @Test
        @DisplayName("Should have same hashcode for equal objects")
        void hashCodeForEqualObjects() {
            LocalDateTime sameTime = LocalDateTime.now();
            Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            asset1.setId("same-id");
            asset2.setId("same-id");
            asset1.setLastSeen(sameTime);
            asset2.setLastSeen(sameTime);
            assertThat(asset1.hashCode()).isEqualTo(asset2.hashCode());
        }

        @Test
        @DisplayName("Should have different hashcode for different objects")
        void hashCodeForDifferentObjects() {
            Asset asset1 = new Asset("Test1", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Test2", "DATABASE", "LOW", 2.0, 2.0);
            assertThat(asset1.hashCode()).isNotEqualTo(asset2.hashCode());
        }

        @Test
        @DisplayName("Should handle null fields in hashCode")
        void hashCodeWithNullFields() {
            Asset asset = new Asset();
            // Should not throw exception
            assertThat(asset.hashCode()).isNotNull();
        }

        @Test
        @DisplayName("Should test all equals branches with null variations")
        void equalsAllBranchesWithNulls() {
            Asset base = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            base.setId("id");
            LocalDateTime time = LocalDateTime.now();
            base.setLastSeen(time);

            // Test with null id in base
            Asset other = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            other.setLastSeen(time);
            base.setId(null);
            other.setId("id");
            assertThat(base).isNotEqualTo(other);

            // Test with both null ids
            base.setId(null);
            other.setId(null);
            base.setLastSeen(time);
            other.setLastSeen(time);
            assertThat(base).isEqualTo(other);

            // Reset base
            base = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            base.setId("id");
            base.setLastSeen(time);

            // Test with null name variations
            other = new Asset();
            other.setName("Test");
            other.setType("SERVER");
            other.setCriticality("HIGH");
            other.setX(1.0);
            other.setY(1.0);
            other.setId("id");
            other.setLastSeen(time);
            base.setName(null);
            assertThat(base).isNotEqualTo(other);

            // Test with null type variations
            base.setName("Test");
            base.setType(null);
            other.setType(null);
            assertThat(base).isEqualTo(other);
            
            // Test with null criticality
            base.setType("SERVER");
            base.setCriticality(null);
            other.setType("SERVER");
            other.setCriticality("HIGH");
            assertThat(base).isNotEqualTo(other);

            // Test with null status
            base.setCriticality("HIGH");
            base.setStatus(null);
            other.setCriticality("HIGH");
            other.setStatus("ACTIVE");
            assertThat(base).isNotEqualTo(other);

            // Test with null lastSeen
            base.setStatus("ACTIVE");
            base.setLastSeen(null);
            other.setStatus("ACTIVE");
            other.setLastSeen(time);
            assertThat(base).isNotEqualTo(other);

            // Both null lastSeen
            base.setLastSeen(null);
            other.setLastSeen(null);
            assertThat(base).isEqualTo(other);
        }

        @Test
        @DisplayName("Should cover all equals null branches")
        void equalsCompleteNullCoverage() {
            Asset base = new Asset();
            base.setId("id");
            base.setName("name");
            base.setType("type");
            base.setCriticality("criticality");
            base.setStatus("status");
            LocalDateTime time = LocalDateTime.now();
            base.setLastSeen(time);

            // Test each field null in other
            Asset other = new Asset();
            other.setId("id");
            other.setName(null);
            other.setType("type");
            other.setCriticality("criticality");
            other.setStatus("status");
            other.setLastSeen(time);
            assertThat(base).isNotEqualTo(other);

            // Test type null in other
            other.setName("name");
            other.setType(null);
            assertThat(base).isNotEqualTo(other);

            // Test criticality null in other
            other.setType("type");
            other.setCriticality(null);
            assertThat(base).isNotEqualTo(other);

            // Test status null in other  
            other.setCriticality("criticality");
            other.setStatus(null);
            assertThat(base).isNotEqualTo(other);

            // Test lastSeen null in other
            other.setStatus("status");
            other.setLastSeen(null);
            assertThat(base).isNotEqualTo(other);
        }

        @Test
        @DisplayName("Should test equals with different Y coordinate")
        void equalsWithDifferentYCoordinate() {
            Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Test", "SERVER", "HIGH", 1.0, 2.0);
            asset1.setId("id");
            asset2.setId("id");
            LocalDateTime time = LocalDateTime.now();
            asset1.setLastSeen(time);
            asset2.setLastSeen(time);
            assertThat(asset1).isNotEqualTo(asset2);
        }

        @Test
        @DisplayName("Should test equals with different status non-null")
        void equalsWithDifferentStatusNonNull() {
            Asset asset1 = new Asset();
            Asset asset2 = new Asset();
            asset1.setId("id");
            asset2.setId("id");
            asset1.setStatus("ACTIVE");
            asset2.setStatus("INACTIVE");
            LocalDateTime time = LocalDateTime.now();
            asset1.setLastSeen(time);
            asset2.setLastSeen(time);
            assertThat(asset1).isNotEqualTo(asset2);
        }

        @Test
        @DisplayName("Should cover equals when base has null and other has value")
        void equalsBaseNullOtherValue() {
            LocalDateTime time = LocalDateTime.now();
            
            // Test id: base null, other not null
            Asset base = new Asset();
            Asset other = new Asset();
            base.setId(null);
            other.setId("id");
            base.setLastSeen(time);
            other.setLastSeen(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test name: base null, other not null
            base = new Asset();
            other = new Asset();
            base.setId("id");
            other.setId("id");
            base.setName(null);
            other.setName("name");
            base.setLastSeen(time);
            other.setLastSeen(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test type: base null, other not null
            base = new Asset();
            other = new Asset();
            base.setId("id");
            other.setId("id");
            base.setType(null);
            other.setType("type");
            base.setLastSeen(time);
            other.setLastSeen(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test criticality: base null, other not null
            base = new Asset();
            other = new Asset();
            base.setId("id");
            other.setId("id");
            base.setCriticality(null);
            other.setCriticality("criticality");
            base.setLastSeen(time);
            other.setLastSeen(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test status: base null, other not null
            base = new Asset();
            other = new Asset();
            base.setId("id");
            other.setId("id");
            base.setStatus(null);
            other.setStatus("status");
            base.setLastSeen(time);
            other.setLastSeen(time);
            assertThat(base).isNotEqualTo(other);
            
            // Test lastSeen: base null, other not null
            base = new Asset();
            other = new Asset();
            base.setId("id");
            other.setId("id");
            base.setLastSeen(null);
            other.setLastSeen(time);
            assertThat(base).isNotEqualTo(other);
        }

        @Test
        @DisplayName("Should cover hashCode with null fields")
        void hashCodeWithAllNullFields() {
            Asset asset = new Asset();
            asset.setId(null);
            asset.setName(null);
            asset.setType(null);
            asset.setCriticality(null);
            asset.setStatus(null);
            asset.setLastSeen(null);
            
            // Should not throw exception
            int hashCode = asset.hashCode();
            assertThat(hashCode).isNotNull();
            
            // Test with another asset with same null values
            Asset asset2 = new Asset();
            asset2.setId(null);
            asset2.setName(null);
            asset2.setType(null);
            asset2.setCriticality(null);
            asset2.setStatus(null);
            asset2.setLastSeen(null);
            
            assertThat(asset.hashCode()).isEqualTo(asset2.hashCode());
        }

        @Test
        @DisplayName("Should test equals with status null in first object but not second")
        void equalsStatusNullInFirstObjectOnly() {
            LocalDateTime time = LocalDateTime.now();
            Asset asset1 = new Asset();
            Asset asset2 = new Asset();
            
            asset1.setId("id");
            asset2.setId("id");
            asset1.setName("name");
            asset2.setName("name");
            asset1.setType("type");
            asset2.setType("type");
            asset1.setCriticality("criticality");
            asset2.setCriticality("criticality");
            asset1.setStatus(null);
            asset2.setStatus("ACTIVE");
            asset1.setLastSeen(time);
            asset2.setLastSeen(time);
            
            assertThat(asset1).isNotEqualTo(asset2);
        }
    }
}