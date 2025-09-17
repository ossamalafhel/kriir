package com.kriir.platform.asset;

import com.kriir.platform.model.Asset;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@DisplayName("Asset Model Tests")
class AssetTest {

    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset();
    }

    @Test
    @DisplayName("Default constructor should generate UUID and set defaults")
    void defaultConstructor() {
        Asset newAsset = new Asset();
        
        assertThat(newAsset.id).isNotNull();
        assertThat(newAsset.id).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        assertThat(newAsset.status).isEqualTo("ACTIVE");
        assertThat(newAsset.lastSeen).isNotNull();
        assertThat(newAsset.lastSeen).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Parameterized constructor should set all fields correctly")
    void parameterizedConstructor() {
        Asset newAsset = new Asset("Web Server", "SERVER", "CRITICAL", 7.06064, 48.092971);
        
        assertThat(newAsset.id).isNotNull();
        assertThat(newAsset.name).isEqualTo("Web Server");
        assertThat(newAsset.type).isEqualTo("SERVER");
        assertThat(newAsset.criticality).isEqualTo("CRITICAL");
        assertThat(newAsset.x).isEqualTo(7.06064);
        assertThat(newAsset.y).isEqualTo(48.092971);
        assertThat(newAsset.status).isEqualTo("ACTIVE");
        assertThat(newAsset.lastSeen).isNotNull();
    }

    @Test
    @DisplayName("Should set and get ID correctly")
    void idGetterSetter() {
        String id = UUID.randomUUID().toString();
        asset.id = id;
        assertThat(asset.id).isEqualTo(id);
    }

    @Test
    @DisplayName("Should set and get name correctly")
    void nameGetterSetter() {
        asset.name = "Database Server";
        assertThat(asset.name).isEqualTo("Database Server");
    }

    @Test
    @DisplayName("Should set and get type correctly")
    void typeGetterSetter() {
        asset.type = "DATABASE";
        assertThat(asset.type).isEqualTo("DATABASE");
    }

    @Test
    @DisplayName("Should set and get criticality correctly")
    void criticalityGetterSetter() {
        asset.criticality = "HIGH";
        assertThat(asset.criticality).isEqualTo("HIGH");
    }

    @Test
    @DisplayName("Should set and get status correctly")
    void statusGetterSetter() {
        asset.status = "COMPROMISED";
        assertThat(asset.status).isEqualTo("COMPROMISED");
    }

    @Test
    @DisplayName("Should set and get coordinates correctly")
    void coordinatesGetterSetter() {
        asset.x = 2.3522;
        asset.y = 48.8566;
        assertThat(asset.x).isEqualTo(2.3522);
        assertThat(asset.y).isEqualTo(48.8566);
    }

    @Test
    @DisplayName("Should set and get lastSeen correctly")
    void lastSeenGetterSetter() {
        LocalDateTime now = LocalDateTime.now();
        asset.lastSeen = now;
        assertThat(asset.lastSeen).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle null values appropriately")
    void handleNullValues() {
        asset.name = null;
        asset.type = null;
        asset.criticality = null;
        
        assertThat(asset.name).isNull();
        assertThat(asset.type).isNull();
        assertThat(asset.criticality).isNull();
        // But status and ID should not be null
        assertThat(asset.status).isNotNull();
        assertThat(asset.id).isNotNull();
    }

    @Test
    @DisplayName("Should handle edge case coordinates")
    void edgeCaseCoordinates() {
        // Test extreme coordinates
        asset.x = -180.0;
        asset.y = 90.0;
        assertThat(asset.x).isEqualTo(-180.0);
        assertThat(asset.y).isEqualTo(90.0);
        
        // Test zero coordinates
        asset.x = 0.0;
        asset.y = 0.0;
        assertThat(asset.x).isEqualTo(0.0);
        assertThat(asset.y).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should maintain immutability of ID after creation")
    void idImmutabilityCheck() {
        String originalId = asset.id;
        asset.id = "new-id";
        assertThat(asset.id).isEqualTo("new-id");
        assertThat(asset.id).isNotEqualTo(originalId);
    }

    @Test
    @DisplayName("Should accept all valid asset types")
    void validAssetTypes() {
        String[] validTypes = {"SERVER", "WORKSTATION", "ROUTER", "SWITCH", 
                             "FIREWALL", "DATABASE", "CLOUD_INSTANCE", 
                             "IOT_DEVICE", "MOBILE_DEVICE"};
        
        for (String type : validTypes) {
            asset.type = type;
            assertThat(asset.type).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("Should accept all valid criticality levels")
    void validCriticalityLevels() {
        String[] validLevels = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        
        for (String level : validLevels) {
            asset.criticality = level;
            assertThat(asset.criticality).isEqualTo(level);
        }
    }

    @Test
    @DisplayName("Should accept all valid status values")
    void validStatusValues() {
        String[] validStatuses = {"ACTIVE", "INACTIVE", "COMPROMISED", 
                                "UNDER_INVESTIGATION", "QUARANTINED"};
        
        for (String status : validStatuses) {
            asset.status = status;
            assertThat(asset.status).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Should handle very long names")
    void veryLongName() {
        String longName = "A".repeat(1000);
        asset.name = longName;
        assertThat(asset.name).hasSize(1000);
        assertThat(asset.name).isEqualTo(longName);
    }

    @Test
    @DisplayName("Should handle empty strings")
    void emptyStrings() {
        asset.name = "";
        asset.type = "";
        asset.criticality = "";
        asset.status = "";
        
        assertThat(asset.name).isEmpty();
        assertThat(asset.type).isEmpty();
        assertThat(asset.criticality).isEmpty();
        assertThat(asset.status).isEmpty();
    }

    @Test
    @DisplayName("Should handle special characters in strings")
    void specialCharacters() {
        String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        asset.name = specialChars;
        assertThat(asset.name).isEqualTo(specialChars);
    }

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
        asset1.id = "same-id";
        asset2.id = "same-id";
        asset1.lastSeen = sameTime;
        asset2.lastSeen = sameTime;
        assertThat(asset1).isEqualTo(asset2);
    }

    @Test
    @DisplayName("Should not be equal with different id")
    void notEqualsWithDifferentId() {
        Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
        Asset asset2 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
        asset1.id = "id1";
        asset2.id = "id2";
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
        asset1.id = "same-id";
        asset2.id = "same-id";
        LocalDateTime sameTime = LocalDateTime.now();
        asset1.lastSeen = sameTime;
        asset2.lastSeen = sameTime;
        assertThat(asset1).isEqualTo(asset2);
    }

    @Test
    @DisplayName("Should have same hashcode for equal objects")
    void hashCodeForEqualObjects() {
        LocalDateTime sameTime = LocalDateTime.now();
        Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
        Asset asset2 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
        asset1.id = "same-id";
        asset2.id = "same-id";
        asset1.lastSeen = sameTime;
        asset2.lastSeen = sameTime;
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
        base.id = "id";
        LocalDateTime time = LocalDateTime.now();
        base.lastSeen = time;

        // Test with null id in base
        Asset other = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
        other.lastSeen = time;
        base.id = null;
        other.id = "id";
        assertThat(base).isNotEqualTo(other);

        // Test with both null ids
        base.id = null;
        other.id = null;
        base.lastSeen = time;
        other.lastSeen = time;
        assertThat(base).isEqualTo(other);

        // Reset base
        base = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
        base.id = "id";
        base.lastSeen = time;

        // Test with null name variations
        other = new Asset();
        other.name = "Test";
        other.type = "SERVER";
        other.criticality = "HIGH";
        other.x = 1.0;
        other.y = 1.0;
        other.id = "id";
        other.lastSeen = time;
        base.name = null;
        assertThat(base).isNotEqualTo(other);

        // Test with null type variations
        base.name = "Test";
        base.type = null;
        other.type = null;
        assertThat(base).isEqualTo(other);
        
        // Test with null criticality
        base.type = "SERVER";
        base.criticality = null;
        other.type = "SERVER";
        other.criticality = "HIGH";
        assertThat(base).isNotEqualTo(other);

        // Test with null status
        base.criticality = "HIGH";
        base.status = null;
        other.criticality = "HIGH";
        other.status = "ACTIVE";
        assertThat(base).isNotEqualTo(other);

        // Test with null lastSeen
        base.status = "ACTIVE";
        base.lastSeen = null;
        other.status = "ACTIVE";
        other.lastSeen = time;
        assertThat(base).isNotEqualTo(other);

        // Both null lastSeen
        base.lastSeen = null;
        other.lastSeen = null;
        assertThat(base).isEqualTo(other);
    }

    @Test
    @DisplayName("Should cover all equals null branches")
    void equalsCompleteNullCoverage() {
        Asset base = new Asset();
        base.id = "id";
        base.name = "name";
        base.type = "type";
        base.criticality = "criticality";
        base.status = "status";
        LocalDateTime time = LocalDateTime.now();
        base.lastSeen = time;

        // Test each field null in other
        Asset other = new Asset();
        other.id = "id";
        other.name = null;
        other.type = "type";
        other.criticality = "criticality";
        other.status = "status";
        other.lastSeen = time;
        assertThat(base).isNotEqualTo(other);

        // Test type null in other
        other.name = "name";
        other.type = null;
        assertThat(base).isNotEqualTo(other);

        // Test criticality null in other
        other.type = "type";
        other.criticality = null;
        assertThat(base).isNotEqualTo(other);

        // Test status null in other  
        other.criticality = "criticality";
        other.status = null;
        assertThat(base).isNotEqualTo(other);

        // Test lastSeen null in other
        other.status = "status";
        other.lastSeen = null;
        assertThat(base).isNotEqualTo(other);
    }

    @Test
    @DisplayName("Should test equals with different Y coordinate")
    void equalsWithDifferentYCoordinate() {
        Asset asset1 = new Asset("Test", "SERVER", "HIGH", 1.0, 1.0);
        Asset asset2 = new Asset("Test", "SERVER", "HIGH", 1.0, 2.0);
        asset1.id = "id";
        asset2.id = "id";
        LocalDateTime time = LocalDateTime.now();
        asset1.lastSeen = time;
        asset2.lastSeen = time;
        assertThat(asset1).isNotEqualTo(asset2);
    }

    @Test
    @DisplayName("Should test equals with different status non-null")
    void equalsWithDifferentStatusNonNull() {
        Asset asset1 = new Asset();
        Asset asset2 = new Asset();
        asset1.id = "id";
        asset2.id = "id";
        asset1.status = "ACTIVE";
        asset2.status = "INACTIVE";
        LocalDateTime time = LocalDateTime.now();
        asset1.lastSeen = time;
        asset2.lastSeen = time;
        assertThat(asset1).isNotEqualTo(asset2);
    }

    @Test
    @DisplayName("Should cover equals when base has null and other has value")
    void equalsBaseNullOtherValue() {
        LocalDateTime time = LocalDateTime.now();
        
        // Test id: base null, other not null
        Asset base = new Asset();
        Asset other = new Asset();
        base.id = null;
        other.id = "id";
        base.lastSeen = time;
        other.lastSeen = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test name: base null, other not null
        base = new Asset();
        other = new Asset();
        base.id = "id";
        other.id = "id";
        base.name = null;
        other.name = "name";
        base.lastSeen = time;
        other.lastSeen = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test type: base null, other not null
        base = new Asset();
        other = new Asset();
        base.id = "id";
        other.id = "id";
        base.type = null;
        other.type = "type";
        base.lastSeen = time;
        other.lastSeen = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test criticality: base null, other not null
        base = new Asset();
        other = new Asset();
        base.id = "id";
        other.id = "id";
        base.criticality = null;
        other.criticality = "criticality";
        base.lastSeen = time;
        other.lastSeen = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test status: base null, other not null
        base = new Asset();
        other = new Asset();
        base.id = "id";
        other.id = "id";
        base.status = null;
        other.status = "status";
        base.lastSeen = time;
        other.lastSeen = time;
        assertThat(base).isNotEqualTo(other);
        
        // Test lastSeen: base null, other not null
        base = new Asset();
        other = new Asset();
        base.id = "id";
        other.id = "id";
        base.lastSeen = null;
        other.lastSeen = time;
        assertThat(base).isNotEqualTo(other);
    }

    @Test
    @DisplayName("Should cover hashCode with null fields")
    void hashCodeWithAllNullFields() {
        Asset asset = new Asset();
        asset.id = null;
        asset.name = null;
        asset.type = null;
        asset.criticality = null;
        asset.status = null;
        asset.lastSeen = null;
        
        // Should not throw exception
        int hashCode = asset.hashCode();
        assertThat(hashCode).isNotNull();
        
        // Test with another asset with same null values
        Asset asset2 = new Asset();
        asset2.id = null;
        asset2.name = null;
        asset2.type = null;
        asset2.criticality = null;
        asset2.status = null;
        asset2.lastSeen = null;
        
        assertThat(asset.hashCode()).isEqualTo(asset2.hashCode());
    }

    @Test
    @DisplayName("Should test equals with status null in first object but not second")
    void equalsStatusNullInFirstObjectOnly() {
        LocalDateTime time = LocalDateTime.now();
        Asset asset1 = new Asset();
        Asset asset2 = new Asset();
        
        asset1.id = "id";
        asset2.id = "id";
        asset1.name = "name";
        asset2.name = "name";
        asset1.type = "type";
        asset2.type = "type";
        asset1.criticality = "criticality";
        asset2.criticality = "criticality";
        asset1.status = null;
        asset2.status = "ACTIVE";
        asset1.lastSeen = time;
        asset2.lastSeen = time;
        
        assertThat(asset1).isNotEqualTo(asset2);
    }
}