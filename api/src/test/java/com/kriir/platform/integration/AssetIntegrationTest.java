package com.kriir.platform.integration;

import com.kriir.platform.dto.CreateAssetRequest;
import com.kriir.platform.TestDatasourceConfig;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@QuarkusTestResource(TestDatasourceConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Asset Integration Tests")
class AssetIntegrationTest {

    private String createdAssetId;

    @Test
    @DisplayName("Should create and retrieve asset")
    void createAndRetrieveAsset() {
        CreateAssetRequest request = new CreateAssetRequest(
            "Integration Test Server", "SERVER", "HIGH", 2.3522, 48.8566
        );

        // Create asset
        createdAssetId = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/assets")
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("name", is("Integration Test Server"))
            .body("type", is("SERVER"))
            .body("criticality", is("HIGH"))
            .body("id", notNullValue())
            .extract()
            .jsonPath().getString("id");
    }

    @Test
    @DisplayName("Should get all assets")
    void getAllAssets() {
        // First create an asset to ensure database has data
        CreateAssetRequest request = new CreateAssetRequest(
            "Test Asset for GetAll", "SERVER", "MEDIUM", 1.0, 1.0
        );
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/assets")
            .then()
            .statusCode(201);
        
        // Now get all assets
        given()
            .when()
            .get("/api/assets")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasItem(notNullValue()));
    }

    @Test
    @DisplayName("Should update asset status")
    void updateAssetStatus() {
        // First create an asset
        CreateAssetRequest request = new CreateAssetRequest(
            "Status Test Server", "SERVER", "MEDIUM", 1.0, 1.0
        );
        
        String assetId = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/assets")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath().getString("id");

        // Update status
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "QUARANTINED"))
            .when()
            .patch("/api/assets/{id}/status", assetId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", is("QUARANTINED"));
    }

    @Test
    @DisplayName("Should return 404 for non-existent asset")
    void getNonExistentAsset() {
        given()
            .when()
            .get("/api/assets/non-existent-id")
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should validate request body")
    void validateRequestBody() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of())
            .when()
            .patch("/api/assets/test-id/status")
            .then()
            .statusCode(400);
    }
}