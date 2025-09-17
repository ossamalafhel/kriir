package com.kriir.platform.mapper;

import com.kriir.platform.dto.SecurityIncidentDto;
import com.kriir.platform.dto.CreateSecurityIncidentRequest;
import com.kriir.platform.model.SecurityIncident;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecurityIncidentMapper {
    
    public SecurityIncidentDto toDto(SecurityIncident incident) {
        if (incident == null) return null;
        
        return new SecurityIncidentDto(
            incident.id,
            incident.title,
            incident.type,
            incident.severity,
            incident.status,
            incident.x,
            incident.y,
            incident.description,
            incident.affectedAssetId,
            incident.detectedAt,
            incident.resolvedAt,
            incident.assignedTo
        );
    }
    
    public SecurityIncident toEntity(CreateSecurityIncidentRequest request) {
        if (request == null) return null;
        
        SecurityIncident incident = new SecurityIncident(
            request.title(),
            request.type(),
            request.severity(),
            request.x(),
            request.y(),
            request.description()
        );
        
        if (request.affectedAssetId() != null) {
            incident.affectedAssetId = request.affectedAssetId();
        }
        
        return incident;
    }
    
    public void updateEntity(SecurityIncident incident, CreateSecurityIncidentRequest request) {
        if (incident == null || request == null) return;
        
        incident.title = request.title();
        incident.type = request.type();
        incident.severity = request.severity();
        incident.x = request.x();
        incident.y = request.y();
        incident.description = request.description();
        incident.affectedAssetId = request.affectedAssetId();
    }
}