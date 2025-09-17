package com.kriir.platform.controller;

import com.kriir.platform.dto.AssetDto;
import com.kriir.platform.dto.CreateAssetRequest;
import com.kriir.platform.mapper.AssetMapper;
import com.kriir.platform.model.Asset;
import com.kriir.platform.service.AssetService;
import com.kriir.platform.TestDatasourceConfig;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(TestDatasourceConfig.class)
@DisplayName("AssetController Tests")
class AssetControllerTest {

    @InjectMock
    AssetService assetService;

    @InjectMock
    AssetMapper assetMapper;

    private Asset testAsset;
    private AssetDto testAssetDto;
    private CreateAssetRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        testAsset = new Asset("Test Server", "SERVER", "HIGH", 1.0, 2.0);
        testAsset.id = "test-id";
        testAsset.status = "ACTIVE";
        testAsset.lastSeen = LocalDateTime.now();

        testAssetDto = new AssetDto("test-id", "Test Server", "SERVER", "HIGH", "ACTIVE", 1.0, 2.0, LocalDateTime.now());
        testCreateRequest = new CreateAssetRequest("Test Server", "SERVER", "HIGH", 1.0, 2.0);
    }

    @Test
    @DisplayName("GET /api/assets should return all assets")
    void getAllAssets() {
        // Create a Uni<List> that will work correctly with the new service method
        when(assetService.findAll()).thenReturn(Uni.createFrom().item(List.of(testAsset)));
        when(assetMapper.toDto(any(Asset.class))).thenReturn(testAssetDto);

        given()
            .when()
            .get("/api/assets")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(1))
            .body("[0].id", is("test-id"));

        verify(assetService).findAll();
        verify(assetMapper).toDto(any(Asset.class));
    }

    @Test
    @DisplayName("GET /api/assets/{id} should return asset when found")
    void getAssetById_Found() {
        when(assetService.findById("test-id")).thenReturn(Uni.createFrom().item(testAsset));
        when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

        given()
            .when()
            .get("/api/assets/test-id")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", is("test-id"));

        verify(assetService).findById("test-id");
        verify(assetMapper).toDto(testAsset);
    }

    @Test
    @DisplayName("GET /api/assets/{id} should return 404 when not found")
    void getAssetById_NotFound() {
        when(assetService.findById("unknown-id")).thenReturn(Uni.createFrom().nullItem());

        given()
            .when()
            .get("/api/assets/unknown-id")
            .then()
            .statusCode(404);

        verify(assetService).findById("unknown-id");
        verify(assetMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("GET /api/assets/type/{type} should return assets by type")
    void getAssetsByType() {
        when(assetService.findByType("SERVER")).thenReturn(Uni.createFrom().item(List.of(testAsset)));
        when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

        given()
            .when()
            .get("/api/assets/type/SERVER")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(1));

        verify(assetService).findByType("SERVER");
        verify(assetMapper).toDto(testAsset);
    }

    @Test
    @DisplayName("GET /api/assets/count should return asset count")
    void getAssetCount() {
        when(assetService.count()).thenReturn(Uni.createFrom().item(10L));

        given()
            .when()
            .get("/api/assets/count")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("count", is(10));

        verify(assetService).count();
    }

    @Test
    @DisplayName("POST /api/assets should create new asset")
    void createAsset() {
        when(assetService.create(any(CreateAssetRequest.class))).thenReturn(Uni.createFrom().item(testAsset));
        when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

        given()
            .contentType(ContentType.JSON)
            .body(testCreateRequest)
            .when()
            .post("/api/assets")
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", is("test-id"));

        verify(assetService).create(any(CreateAssetRequest.class));
        verify(assetMapper).toDto(testAsset);
    }

    @Test
    @DisplayName("PUT /api/assets/{id} should update existing asset")
    void updateAsset_Found() {
        when(assetService.update(eq("test-id"), any(CreateAssetRequest.class))).thenReturn(Uni.createFrom().item(testAsset));
        when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

        given()
            .contentType(ContentType.JSON)
            .body(testCreateRequest)
            .when()
            .put("/api/assets/test-id")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", is("test-id"));

        verify(assetService).update(eq("test-id"), any(CreateAssetRequest.class));
        verify(assetMapper).toDto(testAsset);
    }

    @Test
    @DisplayName("PUT /api/assets/{id} should return 404 for non-existent asset")
    void updateAsset_NotFound() {
        when(assetService.update(eq("unknown-id"), any(CreateAssetRequest.class))).thenReturn(Uni.createFrom().nullItem());

        given()
            .contentType(ContentType.JSON)
            .body(testCreateRequest)
            .when()
            .put("/api/assets/unknown-id")
            .then()
            .statusCode(404);

        verify(assetService).update(eq("unknown-id"), any(CreateAssetRequest.class));
        verify(assetMapper, never()).toDto(any(Asset.class));
    }

    @Test
    @DisplayName("PATCH /api/assets/{id}/status should update asset status")
    void updateAssetStatus_Success() {
        when(assetService.updateStatus("test-id", "COMPROMISED")).thenReturn(Uni.createFrom().item(testAsset));
        when(assetMapper.toDto(testAsset)).thenReturn(testAssetDto);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "COMPROMISED"))
            .when()
            .patch("/api/assets/test-id/status")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);

        verify(assetService).updateStatus("test-id", "COMPROMISED");
        verify(assetMapper).toDto(testAsset);
    }

    @Test
    @DisplayName("PATCH /api/assets/{id}/status should return 404 for non-existent asset")
    void updateAssetStatus_NotFound() {
        when(assetService.updateStatus("unknown-id", "COMPROMISED")).thenReturn(Uni.createFrom().nullItem());

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "COMPROMISED"))
            .when()
            .patch("/api/assets/unknown-id/status")
            .then()
            .statusCode(404);

        verify(assetService).updateStatus("unknown-id", "COMPROMISED");
    }

    @Test
    @DisplayName("DELETE /api/assets/{id} should delete asset")
    void deleteAsset() {
        when(assetService.delete("test-id")).thenReturn(Uni.createFrom().item(true));

        given()
            .when()
            .delete("/api/assets/test-id")
            .then()
            .statusCode(204);

        verify(assetService).delete("test-id");
    }
}