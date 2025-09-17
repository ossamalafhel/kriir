package com.kriir.platform.controller;

import com.kriir.platform.dto.AssetDto;
import com.kriir.platform.dto.CreateAssetRequest;
import com.kriir.platform.mapper.AssetMapper;
import com.kriir.platform.service.AssetService;
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

@Path("/api/assets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AssetController {

    @Inject
    AssetService assetService;
    
    @Inject
    AssetMapper assetMapper;

    @GET
    @Path("/test")
    public Uni<Response> testEndpoint() {
        return Uni.createFrom().item(Response.ok("Test endpoint works").build());
    }

    @GET
    public Uni<List<AssetDto>> getAllAssets() {
        return assetService.findAll()
                .onItem().transform(assets -> assets.stream()
                        .map(assetMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/{id}")
    @WithSession
    public Uni<Response> getAssetById(@PathParam("id") String id) {
        return assetService.findById(id)
                .onItem().ifNotNull().transform(asset -> Response.ok(assetMapper.toDto(asset)).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @WithTransaction
    public Uni<Response> createAsset(@Valid CreateAssetRequest request) {
        return assetService.create(request)
                .onItem().transform(asset -> Response.status(Response.Status.CREATED)
                        .entity(assetMapper.toDto(asset))
                        .build());
    }

    @PUT
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> updateAsset(@PathParam("id") String id, @Valid CreateAssetRequest request) {
        return assetService.update(id, request)
                .onItem().ifNotNull().transform(asset -> Response.ok(assetMapper.toDto(asset)).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> deleteAsset(@PathParam("id") String id) {
        return assetService.delete(id)
                .onItem().transform(deleted -> deleted ? 
                        Response.noContent().build() : 
                        Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/type/{type}")
    public Uni<List<AssetDto>> getAssetsByType(@PathParam("type") String type) {
        return assetService.findByType(type)
                .onItem().transform(assets -> assets.stream()
                        .map(assetMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/by-criticality/{criticality}")
    public Uni<List<AssetDto>> getAssetsByCriticality(@PathParam("criticality") String criticality) {
        return assetService.findByCriticality(criticality)
                .onItem().transform(assets -> assets.stream()
                        .map(assetMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/by-status/{status}")
    public Uni<List<AssetDto>> getAssetsByStatus(@PathParam("status") String status) {
        return assetService.findByStatus(status)
                .onItem().transform(assets -> assets.stream()
                        .map(assetMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/in-bounds")
    public Uni<List<AssetDto>> getAssetsInBounds(
            @QueryParam("minX") double minX,
            @QueryParam("minY") double minY,
            @QueryParam("maxX") double maxX,
            @QueryParam("maxY") double maxY) {
        return assetService.findInBounds(minX, minY, maxX, maxY)
                .onItem().transform(assets -> assets.stream()
                        .map(assetMapper::toDto)
                        .toList());
    }

    @GET
    @Path("/count")
    @WithSession
    public Uni<Response> getAssetCount() {
        return assetService.count()
                .onItem().transform(count -> Response.ok(Map.of("count", count)).build());
    }

    @GET
    @Path("/stats")
    @WithSession
    public Uni<Response> getAssetStats() {
        return assetService.getStatistics()
                .onItem().transform(stats -> Response.ok(stats).build());
    }

    @PATCH
    @Path("/{id}/status")
    @WithTransaction
    public Uni<Response> updateAssetStatus(@PathParam("id") String id, Map<String, String> body) {
        if (body == null || body.get("status") == null || body.get("status").isEmpty()) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build());
        }
        String status = body.get("status");
        return assetService.updateStatus(id, status)
                .onItem().ifNotNull().transform(asset -> Response.ok(assetMapper.toDto(asset)).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }
}