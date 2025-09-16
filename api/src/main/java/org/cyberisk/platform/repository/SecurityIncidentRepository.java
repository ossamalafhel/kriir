package org.cyberisk.platform.repository;

import org.cyberisk.platform.model.SecurityIncident;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SecurityIncidentRepository extends ReactiveCrudRepository<SecurityIncident, String> {

    Flux<SecurityIncident> findByStatus(String status);

    Flux<SecurityIncident> findBySeverity(String severity);

    Flux<SecurityIncident> findByType(String type);

    @Query("SELECT * FROM security_incidents WHERE affected_asset_id = :assetId")
    Flux<SecurityIncident> findByAffectedAssetId(String assetId);

    @Query("SELECT * FROM security_incidents WHERE x >= :minX AND x <= :maxX AND y >= :minY AND y <= :maxY")
    Flux<SecurityIncident> findByLocationBounds(double minX, double maxX, double minY, double maxY);

    @Query("SELECT COUNT(*) FROM security_incidents WHERE status = :status")
    Mono<Long> countByStatus(String status);

    @Query("SELECT COUNT(*) FROM security_incidents WHERE severity = :severity")
    Mono<Long> countBySeverity(String severity);
}