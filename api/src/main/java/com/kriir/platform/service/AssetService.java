package com.kriir.platform.service;

import com.kriir.platform.model.Asset;
import com.kriir.platform.dto.CreateAssetRequest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.hibernate.reactive.panache.common.WithSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AssetService {

    @WithSession
    public Uni<List<Asset>> findAll() {
        return Asset.listAll();
    }

    @WithSession
    public Uni<Asset> findById(String id) {
        return Asset.findById(id);
    }

    @WithSession
    public Uni<List<Asset>> findByType(String type) {
        return Asset.<Asset>find("type", type).list();
    }

    @WithSession
    public Uni<List<Asset>> findByCriticality(String criticality) {
        return Asset.<Asset>find("criticality", criticality).list();
    }

    @WithSession
    public Uni<List<Asset>> findByStatus(String status) {
        return Asset.<Asset>find("status", status).list();
    }

    @WithSession
    public Uni<List<Asset>> findInBounds(double minX, double minY, double maxX, double maxY) {
        return Asset.<Asset>find("x >= ?1 and x <= ?2 and y >= ?3 and y <= ?4", minX, maxX, minY, maxY).list();
    }

    @WithTransaction
    public Uni<Asset> create(CreateAssetRequest request) {
        Asset asset = new Asset(
            request.name(),
            request.type(),
            request.criticality(),
            request.x(),
            request.y()
        );
        return asset.persist();
    }

    @WithTransaction
    public Uni<Asset> update(String id, CreateAssetRequest request) {
        return Asset.<Asset>findById(id)
                .onItem().ifNotNull().transform(asset -> {
                    asset.name = request.name();
                    asset.type = request.type();
                    asset.criticality = request.criticality();
                    asset.x = request.x();
                    asset.y = request.y();
                    asset.lastSeen = LocalDateTime.now();
                    return asset;
                })
                .onItem().ifNotNull().call(asset -> asset.persist());
    }

    @WithTransaction
    public Uni<Boolean> delete(String id) {
        return Asset.deleteById(id);
    }

    @WithSession
    public Uni<Long> count() {
        return Asset.count();
    }

    @WithSession
    public Uni<Map<String, Object>> getStatistics() {
        return Uni.combine().all().unis(
                Asset.count(),
                Asset.count("status = 'ACTIVE'"),
                Asset.count("criticality = 'HIGH'")
        ).asTuple().onItem().transform(tuple -> Map.of(
                "total", tuple.getItem1(),
                "active", tuple.getItem2(),
                "highCriticality", tuple.getItem3()
        ));
    }

    public Uni<List<Asset>> findActive() {
        return findByStatus("ACTIVE");
    }

    public Uni<List<Asset>> findInactive() {
        return findByStatus("INACTIVE");
    }

    public Uni<List<Asset>> findCritical() {
        return findByCriticality("HIGH");
    }

    @WithTransaction
    public Uni<Asset> updateLastSeen(String id) {
        return Asset.<Asset>findById(id)
                .onItem().ifNotNull().transform(asset -> {
                    asset.lastSeen = LocalDateTime.now();
                    return asset;
                })
                .onItem().ifNotNull().call(asset -> asset.persist());
    }

    @WithTransaction
    public Uni<Asset> updateStatus(String id, String status) {
        return Asset.<Asset>findById(id)
                .onItem().ifNotNull().transform(asset -> {
                    asset.status = status;
                    return asset;
                })
                .onItem().ifNotNull().call(asset -> asset.persist());
    }
}