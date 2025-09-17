package com.kriir.platform.mapper;

import com.kriir.platform.dto.AssetDto;
import com.kriir.platform.dto.CreateAssetRequest;
import com.kriir.platform.model.Asset;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AssetMapper {
    
    public AssetDto toDto(Asset asset) {
        if (asset == null) return null;
        
        return new AssetDto(
            asset.id,
            asset.name,
            asset.type,
            asset.criticality,
            asset.status,
            asset.x,
            asset.y,
            asset.lastSeen
        );
    }
    
    public Asset toEntity(CreateAssetRequest request) {
        if (request == null) return null;
        
        return new Asset(
            request.name(),
            request.type(),
            request.criticality(),
            request.x(),
            request.y()
        );
    }
    
    public void updateEntity(Asset asset, CreateAssetRequest request) {
        if (asset == null || request == null) return;
        
        asset.name = request.name();
        asset.type = request.type();
        asset.criticality = request.criticality();
        asset.x = request.x();
        asset.y = request.y();
    }
}