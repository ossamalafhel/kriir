package com.kriir.platform.mapper;

import com.kriir.platform.dto.AssetDto;
import com.kriir.platform.dto.CreateAssetRequest;
import com.kriir.platform.model.Asset;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {
    
    public AssetDto toDto(Asset asset) {
        if (asset == null) return null;
        
        return new AssetDto(
            asset.getId(),
            asset.getName(),
            asset.getType(),
            asset.getCriticality(),
            asset.getStatus(),
            asset.getX(),
            asset.getY(),
            asset.getLastSeen()
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
        
        asset.setName(request.name());
        asset.setType(request.type());
        asset.setCriticality(request.criticality());
        asset.setX(request.x());
        asset.setY(request.y());
    }
}