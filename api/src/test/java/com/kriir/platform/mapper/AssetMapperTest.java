package com.kriir.platform.mapper;

import com.kriir.platform.dto.AssetDto;
import com.kriir.platform.dto.CreateAssetRequest;
import com.kriir.platform.model.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AssetMapper Tests")
class AssetMapperTest {
    
    private AssetMapper assetMapper;
    
    @BeforeEach
    void setUp() {
        assetMapper = new AssetMapper();
    }
    
    @Test
    @DisplayName("Should map Asset to AssetDto correctly")
    void testToDto() {
        Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
        asset.setId("test-id");
        asset.setStatus("ACTIVE");
        LocalDateTime now = LocalDateTime.now();
        asset.setLastSeen(now);
        
        AssetDto dto = assetMapper.toDto(asset);
        
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo("test-id");
        assertThat(dto.name()).isEqualTo("Server");
        assertThat(dto.type()).isEqualTo("SERVER");
        assertThat(dto.criticality()).isEqualTo("HIGH");
        assertThat(dto.status()).isEqualTo("ACTIVE");
        assertThat(dto.x()).isEqualTo(1.0);
        assertThat(dto.y()).isEqualTo(2.0);
        assertThat(dto.lastSeen()).isEqualTo(now);
    }
    
    @Test
    @DisplayName("Should handle null Asset")
    void testToDtoWithNull() {
        AssetDto dto = assetMapper.toDto(null);
        assertThat(dto).isNull();
    }
    
    @Test
    @DisplayName("Should map CreateAssetRequest to Asset correctly")
    void testToEntity() {
        CreateAssetRequest request = new CreateAssetRequest(
            "Server", "SERVER", "HIGH", 1.0, 2.0
        );
        
        Asset asset = assetMapper.toEntity(request);
        
        assertThat(asset).isNotNull();
        assertThat(asset.getId()).isNotNull(); // Generated UUID
        assertThat(asset.getName()).isEqualTo("Server");
        assertThat(asset.getType()).isEqualTo("SERVER");
        assertThat(asset.getCriticality()).isEqualTo("HIGH");
        assertThat(asset.getX()).isEqualTo(1.0);
        assertThat(asset.getY()).isEqualTo(2.0);
        assertThat(asset.getStatus()).isEqualTo("ACTIVE"); // Default
        assertThat(asset.getLastSeen()).isNotNull(); // Default
    }
    
    @Test
    @DisplayName("Should handle null CreateAssetRequest")
    void testToEntityWithNull() {
        Asset asset = assetMapper.toEntity(null);
        assertThat(asset).isNull();
    }
    
    @Test
    @DisplayName("Should update Asset from CreateAssetRequest correctly")
    void testUpdateEntity() {
        Asset asset = new Asset("OldName", "OLD_TYPE", "LOW", 0.0, 0.0);
        CreateAssetRequest request = new CreateAssetRequest(
            "NewName", "NEW_TYPE", "HIGH", 1.0, 2.0
        );
        
        assetMapper.updateEntity(asset, request);
        
        assertThat(asset.getName()).isEqualTo("NewName");
        assertThat(asset.getType()).isEqualTo("NEW_TYPE");
        assertThat(asset.getCriticality()).isEqualTo("HIGH");
        assertThat(asset.getX()).isEqualTo(1.0);
        assertThat(asset.getY()).isEqualTo(2.0);
    }
    
    @Test
    @DisplayName("Should handle null parameters in updateEntity")
    void testUpdateEntityWithNulls() {
        Asset asset = new Asset("Name", "TYPE", "HIGH", 1.0, 2.0);
        CreateAssetRequest request = new CreateAssetRequest(
            "NewName", "NEW_TYPE", "LOW", 3.0, 4.0
        );
        
        // Test null asset
        assetMapper.updateEntity(null, request);
        // Should not throw exception
        
        // Test null request
        assetMapper.updateEntity(asset, null);
        // Should not modify asset
        assertThat(asset.getName()).isEqualTo("Name");
        assertThat(asset.getType()).isEqualTo("TYPE");
    }
}