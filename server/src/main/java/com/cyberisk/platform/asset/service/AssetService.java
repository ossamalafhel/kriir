package com.cyberisk.platform.asset.service;

import com.cyberisk.platform.asset.model.Asset;
import com.cyberisk.platform.asset.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    /**
     * Find all assets
     */
    public Flux<Asset> findAll() {
        log.debug("Finding all assets");
        return assetRepository.findAll()
                .doOnNext(asset -> log.trace("Found asset: {}", asset.getName()))
                .doOnError(error -> log.error("Error finding assets", error));
    }

    /**
     * Find all assets as a continuous flux (for real-time streaming)
     */
    public Flux<Asset> findAllAsFlux() {
        return Flux.interval(Duration.ofSeconds(0), Duration.ofSeconds(5))
                .flatMap(tick -> findAll())
                .distinct(Asset::getId)
                .doOnNext(asset -> asset.setLastSeen(LocalDateTime.now()))
                .doOnError(error -> log.error("Error in asset flux", error));
    }

    /**
     * Save a new asset
     */
    public Mono<Asset> save(Asset asset) {
        log.info("Saving asset: {}", asset.getName());
        asset.setLastSeen(LocalDateTime.now());
        return assetRepository.save(asset)
                .doOnSuccess(saved -> log.info("Asset saved with ID: {}", saved.getId()))
                .doOnError(error -> log.error("Error saving asset: {}", asset.getName(), error));
    }

    /**
     * Find asset by ID
     */
    public Mono<Asset> findById(String id) {
        log.debug("Finding asset by ID: {}", id);
        return assetRepository.findById(id)
                .doOnNext(asset -> log.trace("Found asset: {} - {}", asset.getId(), asset.getName()))
                .doOnError(error -> log.error("Error finding asset by ID: {}", id, error))
                .switchIfEmpty(Mono.error(new AssetNotFoundException("Asset not found with ID: " + id)));
    }

    /**
     * Find assets within a specified distance from coordinates
     */
    public Flux<Asset> findAssetsWithinDistance(double x, double y, double distance) {
        log.info("Finding assets within {} meters of coordinates ({}, {})", distance, x, y);
        return assetRepository.findAssetsWithinDistance(x, y, distance)
                .doOnNext(asset -> log.debug("Found nearby asset: {} at ({}, {})", 
                        asset.getName(), asset.getX(), asset.getY()))
                .doOnError(error -> log.error("Error finding assets within distance", error));
    }

    /**
     * Find assets by criticality level
     */
    public Flux<Asset> findByCriticality(Asset.CriticalityLevel criticality) {
        log.debug("Finding assets with criticality: {}", criticality);
        return assetRepository.findByCriticality(criticality)
                .doOnNext(asset -> log.trace("Found {} criticality asset: {}", criticality, asset.getName()))
                .doOnError(error -> log.error("Error finding assets by criticality: {}", criticality, error));
    }

    /**
     * Find assets by status
     */
    public Flux<Asset> findByStatus(Asset.AssetStatus status) {
        log.debug("Finding assets with status: {}", status);
        return assetRepository.findByStatus(status)
                .doOnNext(asset -> log.trace("Found {} status asset: {}", status, asset.getName()))
                .doOnError(error -> log.error("Error finding assets by status: {}", status, error));
    }

    /**
     * Find assets by type
     */
    public Flux<Asset> findByType(Asset.AssetType type) {
        log.debug("Finding assets of type: {}", type);
        return assetRepository.findByType(type)
                .doOnNext(asset -> log.trace("Found {} type asset: {}", type, asset.getName()))
                .doOnError(error -> log.error("Error finding assets by type: {}", type, error));
    }

    /**
     * Update asset status
     */
    public Mono<Asset> updateStatus(String id, Asset.AssetStatus status) {
        log.info("Updating asset {} status to {}", id, status);
        return findById(id)
                .map(asset -> {
                    asset.setStatus(status);
                    asset.setLastSeen(LocalDateTime.now());
                    return asset;
                })
                .flatMap(assetRepository::save)
                .doOnSuccess(updated -> log.info("Asset {} status updated to {}", updated.getId(), status))
                .doOnError(error -> log.error("Error updating asset {} status", id, error));
    }

    /**
     * Update asset last seen timestamp
     */
    public Mono<Asset> updateLastSeen(String id) {
        log.debug("Updating last seen timestamp for asset: {}", id);
        return findById(id)
                .map(asset -> {
                    asset.setLastSeen(LocalDateTime.now());
                    return asset;
                })
                .flatMap(assetRepository::save)
                .doOnSuccess(updated -> log.debug("Updated last seen for asset: {}", updated.getId()))
                .doOnError(error -> log.error("Error updating last seen for asset: {}", id, error));
    }

    /**
     * Delete asset by ID
     */
    public Mono<Void> deleteById(String id) {
        log.info("Deleting asset: {}", id);
        return assetRepository.deleteById(id)
                .doOnSuccess(v -> log.info("Asset deleted: {}", id))
                .doOnError(error -> log.error("Error deleting asset: {}", id, error));
    }

    /**
     * Count assets by status
     */
    public Mono<Long> countByStatus(Asset.AssetStatus status) {
        log.debug("Counting assets with status: {}", status);
        return assetRepository.countByStatus(status)
                .doOnNext(count -> log.debug("Found {} assets with status {}", count, status))
                .doOnError(error -> log.error("Error counting assets by status: {}", status, error));
    }

    /**
     * Count assets by criticality
     */
    public Mono<Long> countByCriticality(Asset.CriticalityLevel criticality) {
        log.debug("Counting assets with criticality: {}", criticality);
        return assetRepository.countByCriticality(criticality)
                .doOnNext(count -> log.debug("Found {} assets with criticality {}", count, criticality))
                .doOnError(error -> log.error("Error counting assets by criticality: {}", criticality, error));
    }

    /**
     * Find assets that haven't been seen for a specified duration
     */
    public Flux<Asset> findStaleAssets(Duration staleDuration) {
        LocalDateTime threshold = LocalDateTime.now().minus(staleDuration);
        log.info("Finding assets not seen since: {}", threshold);
        return assetRepository.findByLastSeenBefore(threshold)
                .doOnNext(asset -> log.warn("Found stale asset: {} (last seen: {})", 
                        asset.getName(), asset.getLastSeen()))
                .doOnError(error -> log.error("Error finding stale assets", error));
    }

    /**
     * Custom exception for asset not found
     */
    public static class AssetNotFoundException extends RuntimeException {
        public AssetNotFoundException(String message) {
            super(message);
        }
    }
}
