package com.cyberisk.platform.asset.controller;

import com.cyberisk.platform.asset.model.Asset;
import com.cyberisk.platform.asset.service.AssetService;
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

@Slf4j
@RestController
@RequestMapping("/assets")
@Validated
@Tag(name = "Assets", description = "IT Asset management operations")
@CrossOrigin(origins = "*")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @Operation(
            summary = "Get all assets",
            description = "Retrieve all IT assets in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assets retrieved successfully")
    })
    @GetMapping
    public Flux<Asset> getAllAssets() {
        log.info("Retrieving all assets");
        return assetService.findAll();
    }

    @Operation(
            summary = "Create new asset",
            description = "Create a new IT asset with geospatial coordinates"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asset created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid asset data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Asset> createAsset(
            @Parameter(description = "Asset creation request")
            @Valid @RequestBody Asset asset) {
        log.info("Creating new asset: {}", asset.getName());
        return assetService.save(asset);
    }

    @Operation(
            summary = "Real-time asset updates",
            description = "Server-Sent Events stream for real-time asset status updates"
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Asset> getAssetStream() {
        log.info("Starting asset real-time stream");
        return assetService.findAllAsFlux()
                .delayElements(Duration.ofSeconds(2))
                .doOnNext(asset -> log.debug("Streaming asset: {}", asset.getId()))
                .doOnError(error -> log.error("Error in asset stream", error));
    }

    @Operation(
            summary = "Get assets within area",
            description = "Find assets within a specified distance from coordinates"
    )
    @GetMapping("/nearby")
    public Flux<Asset> getAssetsNearby(
            @Parameter(description = "Longitude coordinate", example = "7.06064")
            @RequestParam @NotNull Double x,
            @Parameter(description = "Latitude coordinate", example = "48.092971")
            @RequestParam @NotNull Double y,
            @Parameter(description = "Search radius in meters", example = "1000")
            @RequestParam @NotNull Double distance) {
        log.info("Finding assets near coordinates ({}, {}) within {} meters", x, y, distance);
        return assetService.findAssetsWithinDistance(x, y, distance);
    }

    @Operation(
            summary = "Get asset by ID",
            description = "Retrieve a specific asset by its ID"
    )
    @GetMapping("/{id}")
    public Mono<Asset> getAssetById(
            @Parameter(description = "Asset ID")
            @PathVariable String id) {
        log.info("Retrieving asset with ID: {}", id);
        return assetService.findById(id);
    }

    @Operation(
            summary = "Update asset status",
            description = "Update the status of an existing asset"
    )
    @PatchMapping("/{id}/status")
    public Mono<Asset> updateAssetStatus(
            @Parameter(description = "Asset ID")
            @PathVariable String id,
            @Parameter(description = "New status")
            @RequestParam Asset.AssetStatus status) {
        log.info("Updating asset {} status to {}", id, status);
        return assetService.updateStatus(id, status);
    }

    @Operation(
            summary = "Get assets by criticality",
            description = "Filter assets by their criticality level"
    )
    @GetMapping("/by-criticality/{criticality}")
    public Flux<Asset> getAssetsByCriticality(
            @Parameter(description = "Criticality level")
            @PathVariable Asset.CriticalityLevel criticality) {
        log.info("Retrieving assets with criticality: {}", criticality);
        return assetService.findByCriticality(criticality);
    }

    @Operation(
            summary = "Delete asset",
            description = "Remove an asset from the system"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAsset(
            @Parameter(description = "Asset ID")
            @PathVariable String id) {
        log.info("Deleting asset: {}", id);
        return assetService.deleteById(id);
    }
}
