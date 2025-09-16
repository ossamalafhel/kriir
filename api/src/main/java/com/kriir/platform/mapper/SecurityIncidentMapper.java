package com.kriir.platform.mapper;

import com.kriir.platform.dto.SecurityIncidentDto;
import com.kriir.platform.dto.CreateSecurityIncidentRequest;
import com.kriir.platform.model.SecurityIncident;
import org.springframework.stereotype.Component;

@Component
public class SecurityIncidentMapper {
    
    public SecurityIncidentDto toDto(SecurityIncident incident) {
        if (incident == null) return null;
        
        return new SecurityIncidentDto(
            incident.getId(),
            incident.getTitle(),
            incident.getType(),
            incident.getSeverity(),
            incident.getStatus(),
            incident.getX(),
            incident.getY(),
            incident.getDescription(),
            incident.getAffectedAssetId(),
            incident.getDetectedAt(),
            incident.getResolvedAt(),
            incident.getAssignedTo()
        );
    }
    
    public SecurityIncident toEntity(CreateSecurityIncidentRequest request) {
        if (request == null) return null;
        
        SecurityIncident incident = new SecurityIncident(
            request.title(),
            request.type(),
            request.severity(),
            request.x(),
            request.y()
        );
        incident.setDescription(request.description());
        incident.setAffectedAssetId(request.affectedAssetId());
        
        return incident;
    }
    
    public void updateEntity(SecurityIncident incident, CreateSecurityIncidentRequest request) {
        if (incident == null || request == null) return;
        
        incident.setTitle(request.title());
        incident.setType(request.type());
        incident.setSeverity(request.severity());
        incident.setX(request.x());
        incident.setY(request.y());
        incident.setDescription(request.description());
        incident.setAffectedAssetId(request.affectedAssetId());
    }
}