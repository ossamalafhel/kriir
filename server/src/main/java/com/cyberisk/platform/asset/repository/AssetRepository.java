package com.cyberisk.platform.asset.repository;

import com.cyberisk.platform.asset.model.Asset;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Reactive repository for Asset entities with geospatial capabilities
 */
@Repository
public interface AssetRepository extends ReactiveCrudRepository<Asset, String> {

    /**
     * Find assets by criticality level
     */
    Flux<Asset> findByCriticality(Asset.CriticalityLevel criticality);

    /**
     * Find assets by status
     */
    Flux<Asset> findByStatus(Asset.AssetStatus status);

    /**
     * Find assets by type
     */
    Flux<Asset> findByType(Asset.AssetType type);

    /**
     * Count assets by status
     */
    Mono<Long> countByStatus(Asset.AssetStatus status);

    /**
     * Count assets by criticality level
     */
    Mono<Long> countByCriticality(Asset.CriticalityLevel criticality);

    /**
     * Find assets that haven't been seen since a specific time
     */
    Flux<Asset> findByLastSeenBefore(LocalDateTime threshold);

    /**
     * Find assets within a specified distance from coordinates using PostGIS
     * Uses ST_DWithin function for efficient geospatial queries
     */
    @Query("""
            SELECT a.* FROM asset a 
            WHERE ST_DWithin(
                ST_SetSRID(ST_MakePoint(a.x, a.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326),
                :distance
            )
            ORDER BY ST_Distance(
                ST_SetSRID(ST_MakePoint(a.x, a.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326)
            )
            """)
    Flux<Asset> findAssetsWithinDistance(double x, double y, double distance);

    /**
     * Find assets within a geographic bounding box
     */
    @Query("""
            SELECT a.* FROM asset a 
            WHERE a.x BETWEEN :minX AND :maxX 
            AND a.y BETWEEN :minY AND :maxY
            """)
    Flux<Asset> findAssetsInBoundingBox(double minX, double minY, double maxX, double maxY);

    /**
     * Find nearest assets to a point, limited by count
     */
    @Query("""
            SELECT a.* FROM asset a 
            ORDER BY ST_Distance(
                ST_SetSRID(ST_MakePoint(a.x, a.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326)
            )
            LIMIT :limit
            """)
    Flux<Asset> findNearestAssets(double x, double y, int limit);

    /**
     * Find assets by name pattern (case insensitive)
     */
    @Query("SELECT * FROM asset WHERE LOWER(name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    Flux<Asset> findByNameContainingIgnoreCase(String namePattern);

    /**
     * Find high criticality assets within distance
     */
    @Query("""
            SELECT a.* FROM asset a 
            WHERE a.criticality IN ('HIGH', 'CRITICAL')
            AND ST_DWithin(
                ST_SetSRID(ST_MakePoint(a.x, a.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326),
                :distance
            )
            ORDER BY a.criticality DESC, ST_Distance(
                ST_SetSRID(ST_MakePoint(a.x, a.y), 4326),
                ST_SetSRID(ST_MakePoint(:x, :y), 4326)
            )
            """)
    Flux<Asset> findHighCriticalityAssetsNearby(double x, double y, double distance);

    /**
     * Count assets by type and status
     */
    Mono<Long> countByTypeAndStatus(Asset.AssetType type, Asset.AssetStatus status);

    /**
     * Find assets last seen within a time window
     */
    Flux<Asset> findByLastSeenAfter(LocalDateTime since);

    /**
     * Get asset density in a geographic area (count per square km)
     */
    @Query("""
            SELECT COUNT(*) FROM asset a 
            WHERE ST_DWithin(
                ST_SetSRID(ST_MakePoint(a.x, a.y), 4326),
                ST_SetSRID(ST_MakePoint(:centerX, :centerY), 4326),
                :radius
            )
            """)
    Mono<Long> countAssetsInRadius(double centerX, double centerY, double radius);
}
