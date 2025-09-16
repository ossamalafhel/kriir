package com.kriir.platform.service;

import com.kriir.platform.model.SecurityIncident;
import com.kriir.platform.repository.SecurityIncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class SecurityIncidentService {

    private final SecurityIncidentRepository securityIncidentRepository;

    @Autowired
    public SecurityIncidentService(SecurityIncidentRepository securityIncidentRepository) {
        this.securityIncidentRepository = securityIncidentRepository;
    }

    public Flux<SecurityIncident> findAll() {
        return securityIncidentRepository.findAll();
    }

    public Mono<SecurityIncident> findById(String id) {
        return securityIncidentRepository.findById(id);
    }

    public Flux<SecurityIncident> findByStatus(String status) {
        return securityIncidentRepository.findByStatus(status);
    }

    public Flux<SecurityIncident> findBySeverity(String severity) {
        return securityIncidentRepository.findBySeverity(severity);
    }

    public Flux<SecurityIncident> findByType(String type) {
        return securityIncidentRepository.findByType(type);
    }

    public Flux<SecurityIncident> findByAffectedAssetId(String assetId) {
        return securityIncidentRepository.findByAffectedAssetId(assetId);
    }

    public Flux<SecurityIncident> findByLocationBounds(double minX, double maxX, double minY, double maxY) {
        return securityIncidentRepository.findByLocationBounds(minX, maxX, minY, maxY);
    }

    public Flux<SecurityIncident> findOpenIncidents() {
        return findByStatus("OPEN");
    }

    public Flux<SecurityIncident> findCriticalIncidents() {
        return findBySeverity("CRITICAL");
    }

    public Mono<SecurityIncident> save(SecurityIncident incident) {
        if (incident.getDetectedAt() == null) {
            incident.setDetectedAt(LocalDateTime.now());
        }
        return securityIncidentRepository.save(incident);
    }

    public Mono<SecurityIncident> update(String id, SecurityIncident incident) {
        return securityIncidentRepository.findById(id)
            .flatMap(existing -> {
                incident.setId(id);
                if (incident.getDetectedAt() == null) {
                    incident.setDetectedAt(existing.getDetectedAt());
                }
                return securityIncidentRepository.save(incident);
            });
    }

    public Mono<Void> deleteById(String id) {
        return securityIncidentRepository.deleteById(id);
    }

    public Mono<Long> count() {
        return securityIncidentRepository.count();
    }

    public Mono<Long> countByStatus(String status) {
        return securityIncidentRepository.countByStatus(status);
    }

    public Mono<Long> countBySeverity(String severity) {
        return securityIncidentRepository.countBySeverity(severity);
    }

    public Mono<SecurityIncident> updateStatus(String id, String newStatus) {
        return securityIncidentRepository.findById(id)
            .flatMap(incident -> {
                incident.setStatus(newStatus);
                if ("RESOLVED".equals(newStatus) || "CLOSED".equals(newStatus)) {
                    incident.setResolvedAt(LocalDateTime.now());
                }
                return securityIncidentRepository.save(incident);
            });
    }

    public Mono<SecurityIncident> assignTo(String id, String assignee) {
        return securityIncidentRepository.findById(id)
            .flatMap(incident -> {
                incident.setAssignedTo(assignee);
                if (!"IN_PROGRESS".equals(incident.getStatus())) {
                    incident.setStatus("IN_PROGRESS");
                }
                return securityIncidentRepository.save(incident);
            });
    }

    public Mono<SecurityIncident> resolve(String id, String resolution) {
        return securityIncidentRepository.findById(id)
            .flatMap(incident -> {
                incident.setStatus("RESOLVED");
                incident.setResolvedAt(LocalDateTime.now());
                if (resolution != null && !resolution.isEmpty()) {
                    String currentDesc = incident.getDescription();
                    incident.setDescription(currentDesc != null ? 
                        currentDesc + "\n\nResolution: " + resolution : 
                        "Resolution: " + resolution);
                }
                return securityIncidentRepository.save(incident);
            });
    }
}