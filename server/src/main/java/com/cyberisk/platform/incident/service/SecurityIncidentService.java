package com.cyberisk.platform.incident.service;

import com.cyberisk.platform.incident.model.SecurityIncident;
import com.cyberisk.platform.incident.repository.SecurityIncidentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SecurityIncidentService {

    private final SecurityIncidentRepository incidentRepository;

    public SecurityIncidentService(SecurityIncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    /**
     * Find all security incidents
     */
    public Flux<SecurityIncident> findAll() {
        log.debug("Finding all security incidents");
        return incidentRepository.findAll()
                .doOnNext(incident -> log.trace("Found incident: {}", incident.getTitle()))
                .doOnError(error -> log.error("Error finding incidents", error));
    }

    /**
     * Find all incidents as a continuous flux (for real-time streaming)
     */
    public Flux<SecurityIncident> findAllAsFlux() {
        return Flux.interval(Duration.ofSeconds(0), Duration.ofSeconds(10))
                .flatMap(tick -> findAll())
                .distinct(SecurityIncident::getId)
                .doOnError(error -> log.error("Error in incident flux", error));
    }

    /**
     * Save a new security incident
     */
    public Mono<SecurityIncident> save(SecurityIncident incident) {
        log.info("Saving security incident: {}", incident.getTitle());
        incident.setDetectedAt(LocalDateTime.now());
        return incidentRepository.save(incident)
                .doOnSuccess(saved -> log.info("Incident saved with ID: {}", saved.getId()))
                .doOnError(error -> log.error("Error saving incident: {}", incident.getTitle(), error));
    }

    /**
     * Find incident by ID
     */
    public Mono<SecurityIncident> findById(String id) {
        log.debug("Finding security incident by ID: {}", id);
        return incidentRepository.findById(id)
                .doOnNext(incident -> log.trace("Found incident: {} - {}", incident.getId(), incident.getTitle()))
                .doOnError(error -> log.error("Error finding incident by ID: {}", id, error))
                .switchIfEmpty(Mono.error(new SecurityIncidentNotFoundException("Incident not found with ID: " + id)));
    }

    /**
     * Find incidents within a specified distance from coordinates
     */
    public Flux<SecurityIncident> findIncidentsWithinDistance(double x, double y, double distance) {
        log.info("Finding incidents within {} meters of coordinates ({}, {})", distance, x, y);
        return incidentRepository.findIncidentsWithinDistance(x, y, distance)
                .doOnNext(incident -> log.debug("Found nearby incident: {} at ({}, {})", 
                        incident.getTitle(), incident.getX(), incident.getY()))
                .doOnError(error -> log.error("Error finding incidents within distance", error));
    }

    /**
     * Find incidents by severity level
     */
    public Flux<SecurityIncident> findBySeverity(SecurityIncident.SeverityLevel severity) {
        log.debug("Finding incidents with severity: {}", severity);
        return incidentRepository.findBySeverity(severity)
                .doOnNext(incident -> log.trace("Found {} severity incident: {}", severity, incident.getTitle()))
                .doOnError(error -> log.error("Error finding incidents by severity: {}", severity, error));
    }

    /**
     * Find incidents by status
     */
    public Flux<SecurityIncident> findByStatus(SecurityIncident.IncidentStatus status) {
        log.debug("Finding incidents with status: {}", status);
        return incidentRepository.findByStatus(status)
                .doOnNext(incident -> log.trace("Found {} status incident: {}", status, incident.getTitle()))
                .doOnError(error -> log.error("Error finding incidents by status: {}", status, error));
    }

    /**
     * Find incidents by type
     */
    public Flux<SecurityIncident> findByType(SecurityIncident.IncidentType type) {
        log.debug("Finding incidents of type: {}", type);
        return incidentRepository.findByType(type)
                .doOnNext(incident -> log.trace("Found {} type incident: {}", type, incident.getTitle()))
                .doOnError(error -> log.error("Error finding incidents by type: {}", type, error));
    }

    /**
     * Update incident status
     */
    public Mono<SecurityIncident> updateStatus(String id, SecurityIncident.IncidentStatus status, String assignedTo) {
        log.info("Updating incident {} status to {} (assigned to: {})", id, status, assignedTo);
        return findById(id)
                .map(incident -> {
                    incident.setStatus(status);
                    if (assignedTo != null) {
                        incident.setAssignedTo(assignedTo);
                    }
                    if (status == SecurityIncident.IncidentStatus.RESOLVED) {
                        incident.setResolvedAt(LocalDateTime.now());
                    }
                    return incident;
                })
                .flatMap(incidentRepository::save)
                .doOnSuccess(updated -> log.info("Incident {} status updated to {}", updated.getId(), status))
                .doOnError(error -> log.error("Error updating incident {} status", id, error));
    }

    /**
     * Resolve incident with timestamp and notes
     */
    public Mono<SecurityIncident> resolveIncident(String id, LocalDateTime resolvedAt, String notes) {
        log.info("Resolving incident: {} at {}", id, resolvedAt);
        return findById(id)
                .map(incident -> {
                    incident.setStatus(SecurityIncident.IncidentStatus.RESOLVED);
                    incident.setResolvedAt(resolvedAt);
                    if (notes != null) {
                        incident.setDescription(incident.getDescription() + "\n\nResolution: " + notes);
                    }
                    return incident;
                })
                .flatMap(incidentRepository::save)
                .doOnSuccess(resolved -> log.info("Incident resolved: {}", resolved.getId()))
                .doOnError(error -> log.error("Error resolving incident: {}", id, error));
    }

    /**
     * Get incident statistics
     */
    public Mono<IncidentStats> getIncidentStats() {
        log.debug("Calculating incident statistics");
        
        Mono<Long> totalIncidents = incidentRepository.count();
        
        Mono<Long> openIncidents = incidentRepository.countByStatus(
                SecurityIncident.IncidentStatus.OPEN)
                .zipWith(incidentRepository.countByStatus(
                        SecurityIncident.IncidentStatus.IN_PROGRESS))
                .map(tuple -> tuple.getT1() + tuple.getT2());
        
        Mono<Long> criticalIncidents = incidentRepository.countBySeverity(
                SecurityIncident.SeverityLevel.CRITICAL);
        
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        Mono<Long> resolvedToday = incidentRepository.countByStatusAndResolvedAtAfter(
                SecurityIncident.IncidentStatus.RESOLVED, todayStart);
        
        return Mono.zip(totalIncidents, openIncidents, criticalIncidents, resolvedToday)
                .map(tuple -> new IncidentStats(
                        tuple.getT1(),  // total
                        tuple.getT2(),  // open
                        tuple.getT3(),  // critical
                        tuple.getT4()   // resolved today
                ))
                .doOnNext(stats -> log.info("Incident stats: total={}, open={}, critical={}, resolved_today={}",
                        stats.totalIncidents(), stats.openIncidents(), stats.criticalIncidents(), stats.resolvedToday()))
                .doOnError(error -> log.error("Error calculating incident stats", error));
    }

    /**
     * Find incidents detected within a time range
     */
    public Flux<SecurityIncident> findByDetectedAtBetween(LocalDateTime start, LocalDateTime end) {
        log.debug("Finding incidents detected between {} and {}", start, end);
        return incidentRepository.findByDetectedAtBetween(start, end)
                .doOnNext(incident -> log.trace("Found incident detected at {}: {}", 
                        incident.getDetectedAt(), incident.getTitle()))
                .doOnError(error -> log.error("Error finding incidents by detection time", error));
    }

    /**
     * Find incidents affecting a specific asset
     */
    public Flux<SecurityIncident> findByAffectedAssetId(String assetId) {
        log.debug("Finding incidents affecting asset: {}", assetId);
        return incidentRepository.findByAffectedAssetId(assetId)
                .doOnNext(incident -> log.trace("Found incident affecting asset {}: {}", 
                        assetId, incident.getTitle()))
                .doOnError(error -> log.error("Error finding incidents by asset ID: {}", assetId, error));
    }

    /**
     * Delete incident by ID
     */
    public Mono<Void> deleteById(String id) {
        log.info("Deleting security incident: {}", id);
        return incidentRepository.deleteById(id)
                .doOnSuccess(v -> log.info("Security incident deleted: {}", id))
                .doOnError(error -> log.error("Error deleting incident: {}", id, error));
    }

    /**
     * Record for incident statistics
     */
    public record IncidentStats(
            long totalIncidents,
            long openIncidents,
            long criticalIncidents,
            long resolvedToday
    ) {}

    /**
     * Custom exception for security incident not found
     */
    public static class SecurityIncidentNotFoundException extends RuntimeException {
        public SecurityIncidentNotFoundException(String message) {
            super(message);
        }
    }
}
