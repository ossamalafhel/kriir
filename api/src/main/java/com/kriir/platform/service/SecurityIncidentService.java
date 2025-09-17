package com.kriir.platform.service;

import com.kriir.platform.model.SecurityIncident;
import com.kriir.platform.dto.CreateSecurityIncidentRequest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.hibernate.reactive.panache.common.WithSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SecurityIncidentService {

    @WithSession
    public Uni<List<SecurityIncident>> findAll() {
        return SecurityIncident.listAll();
    }

    @WithSession
    public Uni<SecurityIncident> findById(String id) {
        return SecurityIncident.findById(id);
    }

    @WithSession
    public Uni<List<SecurityIncident>> findByType(String type) {
        return SecurityIncident.<SecurityIncident>find("type", type).list();
    }

    @WithSession
    public Uni<List<SecurityIncident>> findBySeverity(String severity) {
        return SecurityIncident.<SecurityIncident>find("severity", severity).list();
    }

    @WithSession
    public Uni<List<SecurityIncident>> findByStatus(String status) {
        return SecurityIncident.<SecurityIncident>find("status", status).list();
    }

    @WithSession
    public Uni<List<SecurityIncident>> findInBounds(double minX, double minY, double maxX, double maxY) {
        return SecurityIncident.<SecurityIncident>find("x >= ?1 and x <= ?2 and y >= ?3 and y <= ?4", minX, maxX, minY, maxY).list();
    }

    @WithSession
    public Uni<List<SecurityIncident>> findRecent(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return SecurityIncident.<SecurityIncident>find("detectedAt >= ?1", since).list();
    }

    @WithTransaction
    public Uni<SecurityIncident> create(CreateSecurityIncidentRequest request) {
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
        return incident.persist();
    }

    @WithTransaction
    public Uni<SecurityIncident> update(String id, CreateSecurityIncidentRequest request) {
        return SecurityIncident.<SecurityIncident>findById(id)
                .onItem().ifNotNull().transform(incident -> {
                    incident.title = request.title();
                    incident.type = request.type();
                    incident.severity = request.severity();
                    incident.x = request.x();
                    incident.y = request.y();
                    incident.description = request.description();
                    if (request.affectedAssetId() != null) {
                        incident.affectedAssetId = request.affectedAssetId();
                    }
                    return incident;
                })
                .onItem().ifNotNull().call(incident -> incident.persist());
    }

    @WithTransaction
    public Uni<Boolean> delete(String id) {
        return SecurityIncident.deleteById(id);
    }

    @WithSession
    public Uni<Long> count() {
        return SecurityIncident.count();
    }

    @WithTransaction
    public Uni<SecurityIncident> resolve(String id) {
        return SecurityIncident.<SecurityIncident>findById(id)
                .onItem().ifNotNull().transform(incident -> {
                    incident.status = "RESOLVED";
                    incident.resolvedAt = LocalDateTime.now();
                    return incident;
                })
                .onItem().ifNotNull().call(incident -> incident.persist());
    }

    @WithTransaction
    public Uni<SecurityIncident> assign(String id, String assignee) {
        return SecurityIncident.<SecurityIncident>findById(id)
                .onItem().ifNotNull().transform(incident -> {
                    incident.assignedTo = assignee;
                    if ("OPEN".equals(incident.status)) {
                        incident.status = "ASSIGNED";
                    }
                    return incident;
                })
                .onItem().ifNotNull().call(incident -> incident.persist());
    }

    @WithSession
    public Uni<Map<String, Object>> getStatistics() {
        return Uni.combine().all().unis(
                SecurityIncident.count(),
                SecurityIncident.count("status = 'OPEN'"),
                SecurityIncident.count("severity = 'HIGH'"),
                SecurityIncident.count("resolvedAt IS NOT NULL")
        ).asTuple().onItem().transform(tuple -> Map.of(
                "total", tuple.getItem1(),
                "open", tuple.getItem2(),
                "highSeverity", tuple.getItem3(),
                "resolved", tuple.getItem4()
        ));
    }

    public Uni<List<SecurityIncident>> findOpen() {
        return findByStatus("OPEN");
    }

    public Uni<List<SecurityIncident>> findResolved() {
        return findByStatus("RESOLVED");
    }

    public Uni<List<SecurityIncident>> findHighSeverity() {
        return findBySeverity("HIGH");
    }

    @WithSession
    public Uni<List<SecurityIncident>> findByAsset(String assetId) {
        return SecurityIncident.<SecurityIncident>find("affectedAssetId", assetId).list();
    }

    @WithSession
    public Uni<List<SecurityIncident>> findUnassigned() {
        return SecurityIncident.<SecurityIncident>find("assignedTo IS NULL").list();
    }

    @WithSession
    public Uni<List<SecurityIncident>> findByAffectedAssetId(String assetId) {
        return SecurityIncident.<SecurityIncident>find("affectedAssetId = ?1", assetId).list();
    }

    @WithSession
    public Uni<List<SecurityIncident>> findByLocationBounds(double minX, double maxX, double minY, double maxY) {
        return SecurityIncident.<SecurityIncident>find("x >= ?1 AND x <= ?2 AND y >= ?3 AND y <= ?4", minX, maxX, minY, maxY)
                .list();
    }

    @WithTransaction
    public Uni<SecurityIncident> updateStatus(String id, String status) {
        return SecurityIncident.<SecurityIncident>findById(id)
                .onItem().ifNotNull().transform(incident -> {
                    incident.status = status;
                    if ("RESOLVED".equals(status) && incident.resolvedAt == null) {
                        incident.resolvedAt = LocalDateTime.now();
                    }
                    return incident;
                })
                .onItem().ifNotNull().call(incident -> incident.persist());
    }

    public Uni<List<SecurityIncident>> findOpenIncidents() {
        return findOpen();
    }

    @WithSession
    public Uni<List<SecurityIncident>> findCriticalIncidents() {
        return SecurityIncident.<SecurityIncident>find("severity = 'CRITICAL' OR severity = 'HIGH'")
                .list();
    }

    @WithSession
    public Uni<Long> countByStatus(String status) {
        return SecurityIncident.count("status = ?1", status);
    }

    @WithSession
    public Uni<Long> countBySeverity(String severity) {
        return SecurityIncident.count("severity = ?1", severity);
    }

    @WithTransaction
    public Uni<SecurityIncident> assignTo(String id, String assignedTo) {
        return SecurityIncident.<SecurityIncident>findById(id)
                .onItem().ifNotNull().transform(incident -> {
                    incident.assignedTo = assignedTo;
                    return incident;
                })
                .onItem().ifNotNull().call(incident -> incident.persist());
    }

    @WithTransaction
    public Uni<SecurityIncident> resolve(String id, String resolution) {
        return SecurityIncident.<SecurityIncident>findById(id)
                .onItem().ifNotNull().transform(incident -> {
                    incident.status = "RESOLVED";
                    incident.resolvedAt = LocalDateTime.now();
                    if (resolution != null) {
                        incident.description = incident.description + "\n\nResolution: " + resolution;
                    }
                    return incident;
                })
                .onItem().ifNotNull().call(incident -> incident.persist());
    }

    @WithTransaction
    public Uni<Void> deleteById(String id) {
        return SecurityIncident.deleteById(id).replaceWithVoid();
    }

    @WithTransaction
    public Uni<SecurityIncident> save(SecurityIncident incident) {
        return incident.persist();
    }
}