package com.kriir.platform.repository;

import com.kriir.platform.model.Asset;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AssetRepository extends ReactiveCrudRepository<Asset, String> {

    Flux<Asset> findByType(String type);

    Flux<Asset> findByCriticality(String criticality);

    Flux<Asset> findByStatus(String status);

    @Query("SELECT * FROM asset WHERE x >= :minX AND x <= :maxX AND y >= :minY AND y <= :maxY")
    Flux<Asset> findByLocationBounds(double minX, double maxX, double minY, double maxY);

    @Query("SELECT COUNT(*) FROM asset WHERE type = :type")
    Mono<Long> countByType(String type);

    @Query("SELECT COUNT(*) FROM asset WHERE status = :status")
    Mono<Long> countByStatus(String status);
}