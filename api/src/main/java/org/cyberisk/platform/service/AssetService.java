package org.cyberisk.platform.service;

import org.cyberisk.platform.model.Asset;
import org.cyberisk.platform.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    @Autowired
    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public Flux<Asset> findAll() {
        return assetRepository.findAll();
    }

    public Mono<Asset> findById(String id) {
        return assetRepository.findById(id);
    }

    public Flux<Asset> findByType(String type) {
        return assetRepository.findByType(type);
    }

    public Flux<Asset> findByCriticality(String criticality) {
        return assetRepository.findByCriticality(criticality);
    }

    public Flux<Asset> findByStatus(String status) {
        return assetRepository.findByStatus(status);
    }

    public Flux<Asset> findByLocationBounds(double minX, double maxX, double minY, double maxY) {
        return assetRepository.findByLocationBounds(minX, maxX, minY, maxY);
    }

    public Mono<Asset> save(Asset asset) {
        if (asset.getLastSeen() == null) {
            asset.setLastSeen(LocalDateTime.now());
        }
        return assetRepository.save(asset);
    }

    public Mono<Asset> update(String id, Asset asset) {
        return assetRepository.findById(id)
            .flatMap(existing -> {
                asset.setId(id);
                asset.setLastSeen(LocalDateTime.now());
                return assetRepository.save(asset);
            });
    }

    public Mono<Void> deleteById(String id) {
        return assetRepository.deleteById(id);
    }

    public Mono<Long> count() {
        return assetRepository.count();
    }

    public Mono<Long> countByType(String type) {
        return assetRepository.countByType(type);
    }

    public Mono<Long> countByStatus(String status) {
        return assetRepository.countByStatus(status);
    }

    public Mono<Asset> updateStatus(String id, String newStatus) {
        return assetRepository.findById(id)
            .flatMap(asset -> {
                asset.setStatus(newStatus);
                asset.setLastSeen(LocalDateTime.now());
                return assetRepository.save(asset);
            });
    }

    public Mono<Asset> updateLocation(String id, double x, double y) {
        return assetRepository.findById(id)
            .flatMap(asset -> {
                asset.setX(x);
                asset.setY(y);
                asset.setLastSeen(LocalDateTime.now());
                return assetRepository.save(asset);
            });
    }
}