package com.cyberisk.platform.incident.controller;

import com.cyberisk.platform.incident.model.SecurityIncident;
import com.cyberisk.platform.incident.service.SecurityIncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/incidents")
@Validated
@Tag(name = "Security Incidents", description = "Cyber security incident management operations")
@CrossOrigin(origins = "*")
public class SecurityIncidentController {

    private final SecurityIncidentService incidentService;

    public SecurityIncidentController(SecurityIncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @Operation(
            summary = "Get all security incidents",
            description = "Retrieve all security incidents in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incidents retrieved successfully")
    })
    @GetMapping
    public Flux<SecurityIncident> getAllIncidents() {
        log.info("Retrieving all security incidents");
        return incidentService.findAll();
    }

    @Operation(
            summary = "Create new security incident",
            description = "Report a new security incident with geospatial context"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Incident created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid incident data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SecurityIncident> createIncident(
            @Parameter(description = "Security incident creation request")
            @Valid @RequestBody SecurityIncident incident) {
        log.info("Creating new security incident: {}", incident.getTitle());
        return incidentService.save(incident);
    }

    @Operation(
            summary = "Real-time incident alerts",
            description = "Server-Sent Events stream for real-time security incident alerts"
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SecurityIncident> getIncidentStream() {
        log.info("Starting security incident real-time stream");
        return incidentService.findAllAsFlux()
                .delayElements(Duration.ofSeconds(3))
                .doOnNext(incident -> log.debug("Streaming incident: {}", incident.getId()))
                .doOnError(error -> log.error("Error in incident stream", error));
    }

    @Operation(
            summary = "Get incidents within area",
            description = "Find security incidents within a specified distance from coordinates"
    )
    @GetMapping("/nearby")
    public Flux<SecurityIncident> getIncidentsNearby(
            @Parameter(description = "Longitude coordinate", example = "7.06064")
            @RequestParam @NotNull Double x,
            @Parameter(description = "Latitude coordinate", example = "48.092971")
            @RequestParam @NotNull Double y,
            @Parameter(description = "Search radius in meters", example = "5000")
            @RequestParam @NotNull Double distance) {
        log.info("Finding incidents near coordinates ({}, {}) within {} meters", x, y, distance);
        return incidentService.findIncidentsWithinDistance(x, y, distance);
    }

    @Operation(
            summary = "Get incident by ID",
            description = "Retrieve a specific security incident by its ID"
    )
    @GetMapping("/{id}")
    public Mono<SecurityIncident> getIncidentById(
            @Parameter(description = "Incident ID")
            @PathVariable String id) {
        log.info("Retrieving incident with ID: {}", id);
        return incidentService.findById(id);
    }

    @Operation(
            summary = "Update incident status",
            description = "Update the status of an existing security incident"
    )
    @PatchMapping("/{id}/status")
    public Mono<SecurityIncident> updateIncidentStatus(
            @Parameter(description = "Incident ID")
            @PathVariable String id,
            @Parameter(description = "New status")
            @RequestParam SecurityIncident.IncidentStatus status,
            @Parameter(description = "Assigned analyst")
            @RequestParam(required = false) String assignedTo) {
        log.info("Updating incident {} status to {} (assigned to: {})", id, status, assignedTo);
        return incidentService.updateStatus(id, status, assignedTo);
    }

    @Operation(
            summary = "Get incidents by severity",
            description = "Filter incidents by their severity level"
    )
    @GetMapping("/by-severity/{severity}")
    public Flux<SecurityIncident> getIncidentsBySeverity(
            @Parameter(description = "Severity level")
            @PathVariable SecurityIncident.SeverityLevel severity) {
        log.info("Retrieving incidents with severity: {}", severity);
        return incidentService.findBySeverity(severity);
    }

    @Operation(
            summary = "Get open incidents",
            description = "Retrieve all open security incidents"
    )
    @GetMapping("/open")
    public Flux<SecurityIncident> getOpenIncidents() {
        log.info("Retrieving all open incidents");
        return incidentService.findByStatus(SecurityIncident.IncidentStatus.OPEN)
                .mergeWith(incidentService.findByStatus(SecurityIncident.IncidentStatus.IN_PROGRESS));
    }

    @Operation(
            summary = "Get incidents by type",
            description = "Filter incidents by their type"
    )
    @GetMapping("/by-type/{type}")
    public Flux<SecurityIncident> getIncidentsByType(
            @Parameter(description = "Incident type")
            @PathVariable SecurityIncident.IncidentType type) {
        log.info("Retrieving incidents of type: {}", type);
        return incidentService.findByType(type);
    }

    @Operation(
            summary = "Resolve incident",
            description = "Mark an incident as resolved with resolution timestamp"
    )
    @PatchMapping("/{id}/resolve")
    public Mono<SecurityIncident> resolveIncident(
            @Parameter(description = "Incident ID")
            @PathVariable String id,
            @Parameter(description = "Resolution notes")
            @RequestParam(required = false) String notes) {
        log.info("Resolving incident: {} with notes: {}", id, notes);
        return incidentService.resolveIncident(id, LocalDateTime.now(), notes);
    }

    @Operation(
            summary = "Get incident statistics",
            description = "Get aggregated statistics about security incidents"
    )
    @GetMapping("/stats")
    public Mono<IncidentStats> getIncidentStats() {
        log.info("Retrieving incident statistics");
        return incidentService.getIncidentStats();
    }

    public record IncidentStats(
            long totalIncidents,
            long openIncidents,
            long criticalIncidents,
            long resolvedToday
    ) {}
}
