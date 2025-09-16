package org.cyberisk.platform.controller;

import org.cyberisk.platform.dto.SecurityIncidentDto;
import org.cyberisk.platform.dto.CreateSecurityIncidentRequest;
import org.cyberisk.platform.mapper.SecurityIncidentMapper;
import org.cyberisk.platform.service.SecurityIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "*")
public class SecurityIncidentController {

    private final SecurityIncidentService securityIncidentService;
    private final SecurityIncidentMapper securityIncidentMapper;

    @Autowired
    public SecurityIncidentController(SecurityIncidentService securityIncidentService, SecurityIncidentMapper securityIncidentMapper) {
        this.securityIncidentService = securityIncidentService;
        this.securityIncidentMapper = securityIncidentMapper;
    }

    @GetMapping
    public Flux<SecurityIncidentDto> getAllIncidents() {
        return securityIncidentService.findAll()
            .map(securityIncidentMapper::toDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<SecurityIncidentDto>> getIncidentById(@PathVariable String id) {
        return securityIncidentService.findById(id)
            .map(securityIncidentMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public Flux<SecurityIncidentDto> getIncidentsByStatus(@PathVariable String status) {
        return securityIncidentService.findByStatus(status)
            .map(securityIncidentMapper::toDto);
    }

    @GetMapping("/severity/{severity}")
    public Flux<SecurityIncidentDto> getIncidentsBySeverity(@PathVariable String severity) {
        return securityIncidentService.findBySeverity(severity)
            .map(securityIncidentMapper::toDto);
    }

    @GetMapping("/type/{type}")
    public Flux<SecurityIncidentDto> getIncidentsByType(@PathVariable String type) {
        return securityIncidentService.findByType(type)
            .map(securityIncidentMapper::toDto);
    }

    @GetMapping("/asset/{assetId}")
    public Flux<SecurityIncidentDto> getIncidentsByAsset(@PathVariable String assetId) {
        return securityIncidentService.findByAffectedAssetId(assetId)
            .map(securityIncidentMapper::toDto);
    }

    @GetMapping("/location")
    public Flux<SecurityIncidentDto> getIncidentsByLocation(
            @RequestParam double minX,
            @RequestParam double maxX,
            @RequestParam double minY,
            @RequestParam double maxY) {
        return securityIncidentService.findByLocationBounds(minX, maxX, minY, maxY)
            .map(securityIncidentMapper::toDto);
    }

    @GetMapping("/open")
    public Flux<SecurityIncidentDto> getOpenIncidents() {
        return securityIncidentService.findOpenIncidents()
            .map(securityIncidentMapper::toDto);
    }

    @GetMapping("/critical")
    public Flux<SecurityIncidentDto> getCriticalIncidents() {
        return securityIncidentService.findCriticalIncidents()
            .map(securityIncidentMapper::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SecurityIncidentDto> createIncident(@Valid @RequestBody CreateSecurityIncidentRequest request) {
        return Mono.just(securityIncidentMapper.toEntity(request))
            .flatMap(securityIncidentService::save)
            .map(securityIncidentMapper::toDto);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<SecurityIncidentDto>> updateIncident(
            @PathVariable String id,
            @Valid @RequestBody CreateSecurityIncidentRequest request) {
        return securityIncidentService.findById(id)
            .flatMap(incident -> {
                securityIncidentMapper.updateEntity(incident, request);
                return securityIncidentService.save(incident);
            })
            .map(securityIncidentMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<SecurityIncidentDto>> updateIncidentStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        if (newStatus == null || newStatus.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        return securityIncidentService.updateStatus(id, newStatus)
            .map(securityIncidentMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/assign")
    public Mono<ResponseEntity<SecurityIncidentDto>> assignIncident(
            @PathVariable String id,
            @RequestBody Map<String, String> assignmentUpdate) {
        String assignee = assignmentUpdate.get("assignedTo");
        if (assignee == null || assignee.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        return securityIncidentService.assignTo(id, assignee)
            .map(securityIncidentMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/resolve")
    public Mono<ResponseEntity<SecurityIncidentDto>> resolveIncident(
            @PathVariable String id,
            @RequestBody Map<String, String> resolutionData) {
        String resolution = resolutionData.get("resolution");
        
        return securityIncidentService.resolve(id, resolution)
            .map(securityIncidentMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteIncident(@PathVariable String id) {
        return securityIncidentService.deleteById(id);
    }

    @GetMapping("/count")
    public Mono<Map<String, Long>> getIncidentCount() {
        return securityIncidentService.count()
            .map(count -> Map.of("count", count));
    }

    @GetMapping("/count/status/{status}")
    public Mono<Map<String, Long>> getIncidentCountByStatus(@PathVariable String status) {
        return securityIncidentService.countByStatus(status)
            .map(count -> Map.of("count", count));
    }

    @GetMapping("/count/severity/{severity}")
    public Mono<Map<String, Long>> getIncidentCountBySeverity(@PathVariable String severity) {
        return securityIncidentService.countBySeverity(severity)
            .map(count -> Map.of("count", count));
    }
}