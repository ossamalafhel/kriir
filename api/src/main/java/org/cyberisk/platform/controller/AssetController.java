package org.cyberisk.platform.controller;

import org.cyberisk.platform.dto.AssetDto;
import org.cyberisk.platform.dto.CreateAssetRequest;
import org.cyberisk.platform.mapper.AssetMapper;
import org.cyberisk.platform.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {

    private final AssetService assetService;
    private final AssetMapper assetMapper;

    @Autowired
    public AssetController(AssetService assetService, AssetMapper assetMapper) {
        this.assetService = assetService;
        this.assetMapper = assetMapper;
    }

    @GetMapping
    public Flux<AssetDto> getAllAssets() {
        return assetService.findAll()
            .map(assetMapper::toDto);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AssetDto>> getAssetById(@PathVariable String id) {
        return assetService.findById(id)
            .map(assetMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public Flux<AssetDto> getAssetsByType(@PathVariable String type) {
        return assetService.findByType(type)
            .map(assetMapper::toDto);
    }

    @GetMapping("/criticality/{criticality}")
    public Flux<AssetDto> getAssetsByCriticality(@PathVariable String criticality) {
        return assetService.findByCriticality(criticality)
            .map(assetMapper::toDto);
    }

    @GetMapping("/status/{status}")
    public Flux<AssetDto> getAssetsByStatus(@PathVariable String status) {
        return assetService.findByStatus(status)
            .map(assetMapper::toDto);
    }

    @GetMapping("/location")
    public Flux<AssetDto> getAssetsByLocation(
            @RequestParam double minX,
            @RequestParam double maxX,
            @RequestParam double minY,
            @RequestParam double maxY) {
        return assetService.findByLocationBounds(minX, maxX, minY, maxY)
            .map(assetMapper::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AssetDto> createAsset(@Valid @RequestBody CreateAssetRequest request) {
        return Mono.just(assetMapper.toEntity(request))
            .flatMap(assetService::save)
            .map(assetMapper::toDto);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<AssetDto>> updateAsset(
            @PathVariable String id,
            @Valid @RequestBody CreateAssetRequest request) {
        return assetService.findById(id)
            .flatMap(asset -> {
                assetMapper.updateEntity(asset, request);
                return assetService.save(asset);
            })
            .map(assetMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<AssetDto>> updateAssetStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        if (newStatus == null || newStatus.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        return assetService.updateStatus(id, newStatus)
            .map(assetMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/location")
    public Mono<ResponseEntity<AssetDto>> updateAssetLocation(
            @PathVariable String id,
            @RequestBody Map<String, Double> locationUpdate) {
        Double x = locationUpdate.get("x");
        Double y = locationUpdate.get("y");
        
        if (x == null || y == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        return assetService.updateLocation(id, x, y)
            .map(assetMapper::toDto)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAsset(@PathVariable String id) {
        return assetService.deleteById(id);
    }

    @GetMapping("/count")
    public Mono<Map<String, Long>> getAssetCount() {
        return assetService.count()
            .map(count -> Map.of("count", count));
    }

    @GetMapping("/count/type/{type}")
    public Mono<Map<String, Long>> getAssetCountByType(@PathVariable String type) {
        return assetService.countByType(type)
            .map(count -> Map.of("count", count));
    }

    @GetMapping("/count/status/{status}")
    public Mono<Map<String, Long>> getAssetCountByStatus(@PathVariable String status) {
        return assetService.countByStatus(status)
            .map(count -> Map.of("count", count));
    }
}