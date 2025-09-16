package com.cyberisk.platform.incident.repository;

import com.cyberisk.platform.incident.model.SecurityIncident;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive repository for SecurityIncident entities with geospatial capabilities
 */
@Repository
public interface SecurityIncidentRepository extends ReactiveCrudRepository<SecurityIncident, String> {

    /**
     * Find incidents by severity level
     */
    Flux<SecurityIncident> findBySeverity(SecurityIncident.SeverityLevel severity);

    /**
     * Find incidents by status
     */
    Flux<SecurityIncident> findByStatus(SecurityIncident.IncidentStatus status);

    /**
     * Find incidents by type
     */
    Flux<SecurityIncident> findByType(SecurityIncident.IncidentType type);

    /**
     * Count incidents by status
     */
    Mono<Long> countByStatus(SecurityIncident.IncidentStatus status);

    /**
     * Count incidents by severity level
     */
    Mono<Long> countBySeverity(SecurityIncident.SeverityLevel severity);

    /**
     * Count resolved incidents after a specific time
     */
    Mono<Long> countByStatusAndResolvedAtAfter(SecurityIncident.IncidentStatus status, LocalDateTime after);

    /**
     * Find incidents detected within a time range
     */
    Flux<SecurityIncident> findByDetectedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find incidents affecting a specific asset
     */
    Flux<SecurityIncident> findByAffectedAssetId(String assetId);

    /**
     * Find incidents assigned to a specific analyst
     */
    Flux<SecurityIncident> findByAssignedTo(String assignedTo);

    /**
     * Find incidents within a specified distance from coordinates using PostGIS
     */
    @Query("""
            SELECT i.* FROM security_incidents i 
            WHERE ST_DWithin(
                ST_SetSRID(ST_MakePoint(i.x, i.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326),
                :distance
            )
            ORDER BY i.detected_at DESC, ST_Distance(
                ST_SetSRID(ST_MakePoint(i.x, i.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326)
            )
            """)
    Flux<SecurityIncident> findIncidentsWithinDistance(double x, double y, double distance);

    /**
     * Find recent incidents (last 24 hours) within geographic area
     */
    @Query("""
            SELECT i.* FROM security_incidents i 
            WHERE i.detected_at >= :since
            AND ST_DWithin(
                ST_SetSRID(ST_MakePoint(i.x, i.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326),
                :distance
            )
            ORDER BY i.detected_at DESC
            """)
    Flux<SecurityIncident> findRecentIncidentsNearby(double x, double y, double distance, LocalDateTime since);

    /**
     * Find critical severity incidents within area
     */
    @Query("""
            SELECT i.* FROM security_incidents i 
            WHERE i.severity = 'CRITICAL'
            AND ST_DWithin(
                ST_SetSRID(ST_MakePoint(i.x, i.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326),
                :distance
            )
            ORDER BY i.detected_at DESC
            """)
    Flux<SecurityIncident> findCriticalIncidentsNearby(double x, double y, double distance);

    /**
     * Find incidents in geographic bounding box
     */
    @Query("""
            SELECT i.* FROM security_incidents i 
            WHERE i.x BETWEEN :minX AND :maxX 
            AND i.y BETWEEN :minY AND :maxY
            ORDER BY i.detected_at DESC
            """)
    Flux<SecurityIncident> findIncidentsInBoundingBox(double minX, double minY, double maxX, double maxY);

    /**
     * Count incidents by type within a geographic area
     */
    @Query("""
            SELECT COUNT(*) FROM security_incidents i 
            WHERE i.type = :type
            AND ST_DWithin(
                ST_SetSRID(ST_MakePoint(i.x, i.y), 4326),
                ST_SetSRID(ST_MakePoint(:centerX, :centerY), 4326),
                :radius
            )
            """)
    Mono<Long> countIncidentsByTypeInRadius(SecurityIncident.IncidentType type, 
                                           double centerX, double centerY, double radius);

    /**
     * Find incident hotspots - areas with high incident density
     */
    @Query("""
            SELECT 
                ROUND(i.x / :gridSize) * :gridSize as grid_x,
                ROUND(i.y / :gridSize) * :gridSize as grid_y,
                COUNT(*) as incident_count
            FROM security_incidents i 
            WHERE i.detected_at >= :since
            GROUP BY ROUND(i.x / :gridSize), ROUND(i.y / :gridSize)
            HAVING COUNT(*) >= :minIncidents
            ORDER BY incident_count DESC
            """)
    Flux<IncidentHotspot> findIncidentHotspots(double gridSize, LocalDateTime since, int minIncidents);

    /**
     * Find unresolved incidents older than specified time
     */
    @Query("""
            SELECT i.* FROM security_incidents i 
            WHERE i.status IN ('OPEN', 'IN_PROGRESS')
            AND i.detected_at < :threshold
            ORDER BY i.severity DESC, i.detected_at ASC
            """)
    Flux<SecurityIncident> findStaleIncidents(LocalDateTime threshold);

    /**
     * Count incidents by severity and time period
     */
    @Query("""
            SELECT COUNT(*) FROM security_incidents i 
            WHERE i.severity = :severity
            AND i.detected_at BETWEEN :start AND :end
            """)
    Mono<Long> countBySeverityAndTimePeriod(SecurityIncident.SeverityLevel severity, 
                                           LocalDateTime start, LocalDateTime end);

    /**
     * Find incidents with similar patterns (same type and location proximity)
     */
    @Query("""
            SELECT DISTINCT i2.* FROM security_incidents i1, security_incidents i2 
            WHERE i1.id = :incidentId
            AND i2.id != i1.id
            AND i2.type = i1.type
            AND ST_DWithin(
                ST_SetSRID(ST_MakePoint(i1.x, i1.y), 4326),
                ST_SetSRID(ST_MakePoint(i2.x, i2.y), 4326),
                :proximityThreshold
            )
            AND ABS(EXTRACT(EPOCH FROM (i2.detected_at - i1.detected_at))) <= :timeThresholdSeconds
            ORDER BY i2.detected_at DESC
            """)
    Flux<SecurityIncident> findSimilarIncidents(String incidentId, 
                                               double proximityThreshold, 
                                               long timeThresholdSeconds);

    /**
     * Record for incident hotspot data
     */
    record IncidentHotspot(
            double gridX,
            double gridY,
            long incidentCount
    ) {}
}
