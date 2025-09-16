package com.kriir.platform.controller;

import com.kriir.platform.dto.AssetDto;
import com.kriir.platform.dto.CreateAssetRequest;
import com.kriir.platform.mapper.AssetMapper;
import com.kriir.platform.model.Asset;
import com.kriir.platform.service.AssetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(AssetController.class)
@DisplayName("AssetController Tests")
class AssetControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AssetService assetService;

    @MockBean
    private AssetMapper assetMapper;

    private Asset testAsset;
    private AssetDto testAssetDto;
    private CreateAssetRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        testAsset = new Asset("Test Server", "SERVER", "HIGH", 1.0, 2.0);
        testAsset.setId("test-id");
        testAsset.setStatus("ACTIVE");
        testAsset.setLastSeen(LocalDateTime.now());

        testAssetDto = new AssetDto("test-id", "Test Server", "SERVER", "HIGH", "ACTIVE", 1.0, 2.0, LocalDateTime.now());
        testCreateRequest = new CreateAssetRequest("Test Server", "SERVER", "HIGH", 1.0, 2.0);
    }

    @Nested
    @DisplayName("GET Endpoints")
    class GetEndpoints {

        @Test
        @DisplayName("GET /api/assets should return all assets")
        void getAllAssets() {
            when(assetService.findAll()).thenReturn(Flux.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.get()
                .uri("/api/assets")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AssetDto.class)
                .hasSize(1);

            verify(assetService).findAll();
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("GET /api/assets/{id} should return asset when found")
        void getAssetById_Found() {
            when(assetService.findById("test-id")).thenReturn(Mono.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.get()
                .uri("/api/assets/test-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AssetDto.class);

            verify(assetService).findById("test-id");
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("GET /api/assets/{id} should return 404 when not found")
        void getAssetById_NotFound() {
            when(assetService.findById("unknown-id")).thenReturn(Mono.empty());

            webTestClient.get()
                .uri("/api/assets/unknown-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

            verify(assetService).findById("unknown-id");
            verify(assetMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("GET /api/assets/type/{type} should return assets by type")
        void getAssetsByType() {
            when(assetService.findByType("SERVER")).thenReturn(Flux.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.get()
                .uri("/api/assets/type/SERVER")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AssetDto.class)
                .hasSize(1);

            verify(assetService).findByType("SERVER");
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("GET /api/assets/criticality/{criticality} should return assets by criticality")
        void getAssetsByCriticality() {
            when(assetService.findByCriticality("HIGH")).thenReturn(Flux.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.get()
                .uri("/api/assets/criticality/HIGH")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AssetDto.class)
                .hasSize(1);

            verify(assetService).findByCriticality("HIGH");
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("GET /api/assets/status/{status} should return assets by status")
        void getAssetsByStatus() {
            when(assetService.findByStatus("ACTIVE")).thenReturn(Flux.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.get()
                .uri("/api/assets/status/ACTIVE")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AssetDto.class)
                .hasSize(1);

            verify(assetService).findByStatus("ACTIVE");
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("GET /api/assets/location should return assets by location bounds")
        void getAssetsByLocation() {
            when(assetService.findByLocationBounds(0.0, 2.0, 1.0, 3.0)).thenReturn(Flux.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.get()
                .uri("/api/assets/location?minX=0.0&maxX=2.0&minY=1.0&maxY=3.0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AssetDto.class)
                .hasSize(1);

            verify(assetService).findByLocationBounds(0.0, 2.0, 1.0, 3.0);
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("GET /api/assets/count should return asset count")
        void getAssetCount() {
            when(assetService.count()).thenReturn(Mono.just(10L));

            webTestClient.get()
                .uri("/api/assets/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(10);

            verify(assetService).count();
        }

        @Test
        @DisplayName("GET /api/assets/count/type/{type} should return asset count by type")
        void getAssetCountByType() {
            when(assetService.countByType("SERVER")).thenReturn(Mono.just(5L));

            webTestClient.get()
                .uri("/api/assets/count/type/SERVER")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(5);

            verify(assetService).countByType("SERVER");
        }

        @Test
        @DisplayName("GET /api/assets/count/status/{status} should return asset count by status")
        void getAssetCountByStatus() {
            when(assetService.countByStatus("ACTIVE")).thenReturn(Mono.just(8L));

            webTestClient.get()
                .uri("/api/assets/count/status/ACTIVE")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(8);

            verify(assetService).countByStatus("ACTIVE");
        }
    }

    @Nested
    @DisplayName("POST Endpoints")
    class PostEndpoints {

        @Test
        @DisplayName("POST /api/assets should create new asset")
        void createAsset() {
            when(assetMapper.toEntity(testCreateRequest)).thenReturn(testAsset);
            when(assetService.save(testAsset)).thenReturn(Mono.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.post()
                .uri("/api/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testCreateRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AssetDto.class);

            verify(assetMapper).toEntity(testCreateRequest);
            verify(assetService).save(testAsset);
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("POST /api/assets should return 400 for invalid request")
        void createAsset_InvalidRequest() {
            CreateAssetRequest invalidRequest = new CreateAssetRequest("", "", "", null, null);

            webTestClient.post()
                .uri("/api/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();

            verify(assetMapper, never()).toEntity(any());
            verify(assetService, never()).save(any());
        }
    }

    @Nested
    @DisplayName("PUT Endpoints")
    class PutEndpoints {

        @Test
        @DisplayName("PUT /api/assets/{id} should update existing asset")
        void updateAsset_Found() {
            when(assetService.findById("test-id")).thenReturn(Mono.just(testAsset));
            when(assetService.save(testAsset)).thenReturn(Mono.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.put()
                .uri("/api/assets/test-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testCreateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AssetDto.class);

            verify(assetService).findById("test-id");
            verify(assetMapper).updateEntity(testAsset, testCreateRequest);
            verify(assetService).save(testAsset);
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("PUT /api/assets/{id} should return 404 for non-existent asset")
        void updateAsset_NotFound() {
            when(assetService.findById("unknown-id")).thenReturn(Mono.empty());

            webTestClient.put()
                .uri("/api/assets/unknown-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testCreateRequest)
                .exchange()
                .expectStatus().isNotFound();

            verify(assetService).findById("unknown-id");
            verify(assetMapper, never()).updateEntity(any(Asset.class), any(CreateAssetRequest.class));
            verify(assetService, never()).save(any(Asset.class));
        }
    }

    @Nested
    @DisplayName("PATCH Endpoints")
    class PatchEndpoints {

        @Test
        @DisplayName("PATCH /api/assets/{id}/status should update asset status")
        void updateAssetStatus_Success() {
            when(assetService.updateStatus("test-id", "COMPROMISED")).thenReturn(Mono.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.patch()
                .uri("/api/assets/test-id/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status", "COMPROMISED"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AssetDto.class);

            verify(assetService).updateStatus("test-id", "COMPROMISED");
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("PATCH /api/assets/{id}/status should return 400 for null status")
        void updateAssetStatus_NullStatus() {
            webTestClient.patch()
                .uri("/api/assets/test-id/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("other", "value"))
                .exchange()
                .expectStatus().isBadRequest();

            verify(assetService, never()).updateStatus(any(), any());
        }

        @Test
        @DisplayName("PATCH /api/assets/{id}/status should return 400 for empty status")
        void updateAssetStatus_EmptyStatus() {
            webTestClient.patch()
                .uri("/api/assets/test-id/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status", ""))
                .exchange()
                .expectStatus().isBadRequest();

            verify(assetService, never()).updateStatus(any(), any());
        }

        @Test
        @DisplayName("PATCH /api/assets/{id}/status should return 404 for non-existent asset")
        void updateAssetStatus_NotFound() {
            when(assetService.updateStatus("unknown-id", "COMPROMISED")).thenReturn(Mono.empty());

            webTestClient.patch()
                .uri("/api/assets/unknown-id/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status", "COMPROMISED"))
                .exchange()
                .expectStatus().isNotFound();

            verify(assetService).updateStatus("unknown-id", "COMPROMISED");
        }

        @Test
        @DisplayName("PATCH /api/assets/{id}/location should update asset location")
        void updateAssetLocation_Success() {
            when(assetService.updateLocation("test-id", 3.0, 4.0)).thenReturn(Mono.just(testAsset));
            when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

            webTestClient.patch()
                .uri("/api/assets/test-id/location")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("x", 3.0, "y", 4.0))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AssetDto.class);

            verify(assetService).updateLocation("test-id", 3.0, 4.0);
            verify(assetMapper).toDto(testAsset);
        }

        @Test
        @DisplayName("PATCH /api/assets/{id}/location should return 400 for null x coordinate")
        void updateAssetLocation_NullXCoordinate() {
            webTestClient.patch()
                .uri("/api/assets/test-id/location")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("y", 3.0))
                .exchange()
                .expectStatus().isBadRequest();

            verify(assetService, never()).updateLocation(any(String.class), any(Double.class), any(Double.class));
        }

        @Test
        @DisplayName("PATCH /api/assets/{id}/location should return 400 for null y coordinate")
        void updateAssetLocation_NullYCoordinate() {
            webTestClient.patch()
                .uri("/api/assets/test-id/location")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("x", 3.0))
                .exchange()
                .expectStatus().isBadRequest();

            verify(assetService, never()).updateLocation(any(String.class), any(Double.class), any(Double.class));
        }

        @Test
        @DisplayName("PATCH /api/assets/{id}/location should return 400 for both null coordinates")
        void updateAssetLocation_BothNullCoordinates() {
            webTestClient.patch()
                .uri("/api/assets/test-id/location")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of())
                .exchange()
                .expectStatus().isBadRequest();

            verify(assetService, never()).updateLocation(any(String.class), any(Double.class), any(Double.class));
        }

        @Test
        @DisplayName("PATCH /api/assets/{id}/location should return 404 for non-existent asset")
        void updateAssetLocation_NotFound() {
            when(assetService.updateLocation("unknown-id", 3.0, 4.0)).thenReturn(Mono.empty());

            webTestClient.patch()
                .uri("/api/assets/unknown-id/location")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("x", 3.0, "y", 4.0))
                .exchange()
                .expectStatus().isNotFound();

            verify(assetService).updateLocation("unknown-id", 3.0, 4.0);
        }
    }

    @Nested
    @DisplayName("DELETE Endpoints")
    class DeleteEndpoints {

        @Test
        @DisplayName("DELETE /api/assets/{id} should delete asset")
        void deleteAsset() {
            when(assetService.deleteById("test-id")).thenReturn(Mono.empty());

            webTestClient.delete()
                .uri("/api/assets/test-id")
                .exchange()
                .expectStatus().isNoContent();

            verify(assetService).deleteById("test-id");
        }
    }
}