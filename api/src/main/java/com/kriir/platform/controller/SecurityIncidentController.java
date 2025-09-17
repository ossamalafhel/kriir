package com.kriir.platform.controller;

import com.kriir.platform.dto.SecurityIncidentDto;
import com.kriir.platform.dto.CreateSecurityIncidentRequest;
import com.kriir.platform.mapper.SecurityIncidentMapper;
import com.kriir.platform.service.SecurityIncidentService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.List;

@Path("/api/incidents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SecurityIncidentController {

    @Inject
    SecurityIncidentService securityIncidentService;
    
    @Inject
    SecurityIncidentMapper securityIncidentMapper;

    @GET
    public Uni<List<SecurityIncidentDto>> getAllIncidents() {
        return securityIncidentService.findAll()
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/{id}")
    @WithSession
    public Uni<Response> getIncidentById(@PathParam("id") String id) {
        return securityIncidentService.findById(id)
                .onItem().ifNotNull().transform(incident -> Response.ok(securityIncidentMapper.toDto(incident)).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @WithTransaction
    public Uni<Response> createIncident(@Valid CreateSecurityIncidentRequest request) {
        return securityIncidentService.create(request)
                .onItem().transform(incident -> Response.status(Response.Status.CREATED)
                        .entity(securityIncidentMapper.toDto(incident))
                        .build());
    }

    @PUT
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> updateIncident(@PathParam("id") String id, @Valid CreateSecurityIncidentRequest request) {
        return securityIncidentService.update(id, request)
                .onItem().ifNotNull().transform(incident -> Response.ok(securityIncidentMapper.toDto(incident)).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> deleteIncident(@PathParam("id") String id) {
        return securityIncidentService.delete(id)
                .onItem().transform(deleted -> deleted ? 
                        Response.noContent().build() : 
                        Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/type/{type}")
    public Uni<List<SecurityIncidentDto>> getIncidentsByType(@PathParam("type") String type) {
        return securityIncidentService.findByType(type)
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/severity/{severity}")
    public Uni<List<SecurityIncidentDto>> getIncidentsBySeverity(@PathParam("severity") String severity) {
        return securityIncidentService.findBySeverity(severity)
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/status/{status}")
    public Uni<List<SecurityIncidentDto>> getIncidentsByStatus(@PathParam("status") String status) {
        return securityIncidentService.findByStatus(status)
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/asset/{assetId}")
    public Uni<List<SecurityIncidentDto>> getIncidentsByAsset(@PathParam("assetId") String assetId) {
        return securityIncidentService.findByAffectedAssetId(assetId)
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/location")
    public Uni<List<SecurityIncidentDto>> getIncidentsByLocation(
            @QueryParam("minX") double minX,
            @QueryParam("maxX") double maxX,
            @QueryParam("minY") double minY,
            @QueryParam("maxY") double maxY) {
        return securityIncidentService.findByLocationBounds(minX, maxX, minY, maxY)
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/open")
    public Uni<List<SecurityIncidentDto>> getOpenIncidents() {
        return securityIncidentService.findOpenIncidents()
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/critical")
    public Uni<List<SecurityIncidentDto>> getCriticalIncidents() {
        return securityIncidentService.findCriticalIncidents()
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/recent")
    public Uni<List<SecurityIncidentDto>> getRecentIncidents(@QueryParam("hours") @DefaultValue("24") int hours) {
        return securityIncidentService.findRecent(hours)
                .onItem().transform(incidents -> incidents.stream()
                        .map(securityIncidentMapper::toDto)
                        .toList());
    }


    @POST
    @Path("/{id}/resolve")
    @WithTransaction
    public Uni<Response> resolveIncident(@PathParam("id") String id, Map<String, String> body) {
        String resolution = body != null ? body.get("resolution") : null;
        return securityIncidentService.resolve(id, resolution)
                .onItem().ifNotNull().transform(incident -> Response.ok(securityIncidentMapper.toDto(incident)).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PATCH
    @Path("/{id}/status")
    @WithTransaction
    public Uni<Response> updateIncidentStatus(@PathParam("id") String id, Map<String, String> body) {
        if (body == null || body.get("status") == null || body.get("status").isEmpty()) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build());
        }
        String status = body.get("status");
        return securityIncidentService.updateStatus(id, status)
                .onItem().ifNotNull().transform(incident -> Response.ok(securityIncidentMapper.toDto(incident)).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PATCH
    @Path("/{id}/assign")
    @WithTransaction
    public Uni<Response> assignIncident(@PathParam("id") String id, Map<String, String> body) {
        if (body == null || body.get("assignedTo") == null || body.get("assignedTo").isEmpty()) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build());
        }
        String assignee = body.get("assignedTo");
        return securityIncidentService.assignTo(id, assignee)
                .onItem().ifNotNull().transform(incident -> Response.ok(securityIncidentMapper.toDto(incident)).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/count")
    @WithSession
    public Uni<Response> getIncidentCount() {
        return securityIncidentService.count()
                .onItem().transform(count -> Response.ok(Map.of("count", count)).build());
    }

    @GET
    @Path("/count/status/{status}")
    @WithSession
    public Uni<Response> getIncidentCountByStatus(@PathParam("status") String status) {
        return securityIncidentService.countByStatus(status)
                .onItem().transform(count -> Response.ok(Map.of("count", count)).build());
    }

    @GET
    @Path("/count/severity/{severity}")
    @WithSession
    public Uni<Response> getIncidentCountBySeverity(@PathParam("severity") String severity) {
        return securityIncidentService.countBySeverity(severity)
                .onItem().transform(count -> Response.ok(Map.of("count", count)).build());
    }

    @GET
    @Path("/stats")
    @WithSession
    public Uni<Response> getIncidentStats() {
        return securityIncidentService.getStatistics()
                .onItem().transform(stats -> Response.ok(stats).build());
    }
}