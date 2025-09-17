package com.kriir.platform.controller;

import com.kriir.platform.dto.SecurityIncidentDto;
import com.kriir.platform.dto.CreateSecurityIncidentRequest;
import com.kriir.platform.mapper.SecurityIncidentMapper;
import com.kriir.platform.model.SecurityIncident;
import com.kriir.platform.service.SecurityIncidentService;
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
@DisplayName("SecurityIncidentController Tests")
class SecurityIncidentControllerTest {

    @InjectMock
    SecurityIncidentService securityIncidentService;

    @InjectMock
    SecurityIncidentMapper securityIncidentMapper;

    private SecurityIncident testIncident;
    private SecurityIncidentDto testIncidentDto;
    private CreateSecurityIncidentRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        testIncident = new SecurityIncident("Malware Detection", "MALWARE_DETECTION", "HIGH", 1.0, 2.0, "Test malware detection incident");
        testIncident.id = "test-id";
        testIncident.status = "OPEN";
        testIncident.description = "Test description";
        testIncident.detectedAt = LocalDateTime.now();

        testIncidentDto = new SecurityIncidentDto("test-id", "Malware Detection", "MALWARE_DETECTION", 
            "HIGH", "OPEN", 1.0, 2.0, "Test description", null, LocalDateTime.now(), null, null);
        
        testCreateRequest = new CreateSecurityIncidentRequest("Malware Detection", "MALWARE_DETECTION", 
            "HIGH", 1.0, 2.0, "Test description", null);
    }

    @Test
    @DisplayName("GET /api/incidents should return all incidents")
    void getAllIncidents() {
        when(securityIncidentService.findAll()).thenReturn(Uni.createFrom().item(List.of(testIncident)));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents")
        .then()
            .statusCode(200)
            .body("$", hasSize(1));

        verify(securityIncidentService).findAll();
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/{id} should return incident when found")
    void getIncidentById_Found() {
        when(securityIncidentService.findById("test-id")).thenReturn(Uni.createFrom().item(testIncident));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/test-id")
        .then()
            .statusCode(200);

        verify(securityIncidentService).findById("test-id");
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/{id} should return 404 when not found")
    void getIncidentById_NotFound() {
        when(securityIncidentService.findById("unknown-id")).thenReturn(Uni.createFrom().nullItem());

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/unknown-id")
        .then()
            .statusCode(404);

        verify(securityIncidentService).findById("unknown-id");
        verify(securityIncidentMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("GET /api/incidents/status/{status} should return incidents by status")
    void getIncidentsByStatus() {
        when(securityIncidentService.findByStatus("OPEN")).thenReturn(Uni.createFrom().item(List.of(testIncident)));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/status/OPEN")
        .then()
            .statusCode(200)
            .body("$", hasSize(1));

        verify(securityIncidentService).findByStatus("OPEN");
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/severity/{severity} should return incidents by severity")
    void getIncidentsBySeverity() {
        when(securityIncidentService.findBySeverity("HIGH")).thenReturn(Uni.createFrom().item(List.of(testIncident)));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/severity/HIGH")
        .then()
            .statusCode(200)
            .body("$", hasSize(1));

        verify(securityIncidentService).findBySeverity("HIGH");
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/type/{type} should return incidents by type")
    void getIncidentsByType() {
        when(securityIncidentService.findByType("MALWARE_DETECTION")).thenReturn(Uni.createFrom().item(List.of(testIncident)));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/type/MALWARE_DETECTION")
        .then()
            .statusCode(200)
            .body("$", hasSize(1));

        verify(securityIncidentService).findByType("MALWARE_DETECTION");
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/asset/{assetId} should return incidents by asset")
    void getIncidentsByAsset() {
        when(securityIncidentService.findByAffectedAssetId("asset-123")).thenReturn(Uni.createFrom().item(List.of(testIncident)));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/asset/asset-123")
        .then()
            .statusCode(200)
            .body("$", hasSize(1));

        verify(securityIncidentService).findByAffectedAssetId("asset-123");
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/location should return incidents by location bounds")
    void getIncidentsByLocation() {
        when(securityIncidentService.findByLocationBounds(0.0, 2.0, 1.0, 3.0)).thenReturn(Uni.createFrom().item(List.of(testIncident)));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
            .queryParam("minX", 0.0)
            .queryParam("maxX", 2.0)
            .queryParam("minY", 1.0)
            .queryParam("maxY", 3.0)
        .when()
            .get("/api/incidents/location")
        .then()
            .statusCode(200)
            .body("$", hasSize(1));

        verify(securityIncidentService).findByLocationBounds(0.0, 2.0, 1.0, 3.0);
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/open should return open incidents")
    void getOpenIncidents() {
        when(securityIncidentService.findOpenIncidents()).thenReturn(Uni.createFrom().item(List.of(testIncident)));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/open")
        .then()
            .statusCode(200)
            .body("$", hasSize(1));

        verify(securityIncidentService).findOpenIncidents();
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/critical should return critical incidents")
    void getCriticalIncidents() {
        when(securityIncidentService.findCriticalIncidents()).thenReturn(Uni.createFrom().item(List.of(testIncident)));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/critical")
        .then()
            .statusCode(200)
            .body("$", hasSize(1));

        verify(securityIncidentService).findCriticalIncidents();
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("GET /api/incidents/count should return incident count")
    void getIncidentCount() {
        when(securityIncidentService.count()).thenReturn(Uni.createFrom().item(15L));

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/count")
        .then()
            .statusCode(200)
            .body("count", is(15));

        verify(securityIncidentService).count();
    }

    @Test
    @DisplayName("GET /api/incidents/count/status/{status} should return incident count by status")
    void getIncidentCountByStatus() {
        when(securityIncidentService.countByStatus("OPEN")).thenReturn(Uni.createFrom().item(8L));

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/count/status/OPEN")
        .then()
            .statusCode(200)
            .body("count", is(8));

        verify(securityIncidentService).countByStatus("OPEN");
    }

    @Test
    @DisplayName("GET /api/incidents/count/severity/{severity} should return incident count by severity")
    void getIncidentCountBySeverity() {
        when(securityIncidentService.countBySeverity("HIGH")).thenReturn(Uni.createFrom().item(3L));

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/incidents/count/severity/HIGH")
        .then()
            .statusCode(200)
            .body("count", is(3));

        verify(securityIncidentService).countBySeverity("HIGH");
    }

    @Test
    @DisplayName("POST /api/incidents should create new incident")
    void createIncident() {
        when(securityIncidentService.create(testCreateRequest)).thenReturn(Uni.createFrom().item(testIncident));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .contentType(ContentType.JSON)
            .body(testCreateRequest)
        .when()
            .post("/api/incidents")
        .then()
            .statusCode(201);

        verify(securityIncidentService).create(testCreateRequest);
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("POST /api/incidents should return 400 for invalid request")
    void createIncident_InvalidRequest() {
        CreateSecurityIncidentRequest invalidRequest = new CreateSecurityIncidentRequest("", "", "", null, null, null, null);

        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
        .when()
            .post("/api/incidents")
        .then()
            .statusCode(400);

        verify(securityIncidentService, never()).create(any());
    }

    @Test
    @DisplayName("PUT /api/incidents/{id} should update existing incident")
    void updateIncident_Found() {
        when(securityIncidentService.update("test-id", testCreateRequest)).thenReturn(Uni.createFrom().item(testIncident));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .contentType(ContentType.JSON)
            .body(testCreateRequest)
        .when()
            .put("/api/incidents/test-id")
        .then()
            .statusCode(200);

        verify(securityIncidentService).update("test-id", testCreateRequest);
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("PUT /api/incidents/{id} should return 404 for non-existent incident")
    void updateIncident_NotFound() {
        when(securityIncidentService.update("unknown-id", testCreateRequest)).thenReturn(Uni.createFrom().nullItem());

        given()
            .contentType(ContentType.JSON)
            .body(testCreateRequest)
        .when()
            .put("/api/incidents/unknown-id")
        .then()
            .statusCode(404);

        verify(securityIncidentService).update("unknown-id", testCreateRequest);
    }

    @Test
    @DisplayName("PATCH /api/incidents/{id}/status should update incident status")
    void updateIncidentStatus_Success() {
        when(securityIncidentService.updateStatus("test-id", "RESOLVED")).thenReturn(Uni.createFrom().item(testIncident));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "RESOLVED"))
        .when()
            .patch("/api/incidents/test-id/status")
        .then()
            .statusCode(200);

        verify(securityIncidentService).updateStatus("test-id", "RESOLVED");
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("PATCH /api/incidents/{id}/status should return 400 for null status")
    void updateIncidentStatus_NullStatus() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("other", "value"))
        .when()
            .patch("/api/incidents/test-id/status")
        .then()
            .statusCode(400);

        verify(securityIncidentService, never()).updateStatus(any(), any());
    }

    @Test
    @DisplayName("PATCH /api/incidents/{id}/status should return 400 for empty status")
    void updateIncidentStatus_EmptyStatus() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", ""))
        .when()
            .patch("/api/incidents/test-id/status")
        .then()
            .statusCode(400);

        verify(securityIncidentService, never()).updateStatus(any(), any());
    }

    @Test
    @DisplayName("PATCH /api/incidents/{id}/status should return 404 for non-existent incident")
    void updateIncidentStatus_NotFound() {
        when(securityIncidentService.updateStatus("unknown-id", "RESOLVED")).thenReturn(Uni.createFrom().nullItem());

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "RESOLVED"))
        .when()
            .patch("/api/incidents/unknown-id/status")
        .then()
            .statusCode(404);

        verify(securityIncidentService).updateStatus("unknown-id", "RESOLVED");
    }

    @Test
    @DisplayName("PATCH /api/incidents/{id}/assign should assign incident")
    void assignIncident_Success() {
        when(securityIncidentService.assignTo("test-id", "analyst@company.com")).thenReturn(Uni.createFrom().item(testIncident));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("assignedTo", "analyst@company.com"))
        .when()
            .patch("/api/incidents/test-id/assign")
        .then()
            .statusCode(200);

        verify(securityIncidentService).assignTo("test-id", "analyst@company.com");
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("PATCH /api/incidents/{id}/assign should return 400 for null assignee")
    void assignIncident_NullAssignee() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("other", "value"))
        .when()
            .patch("/api/incidents/test-id/assign")
        .then()
            .statusCode(400);

        verify(securityIncidentService, never()).assignTo(any(), any());
    }

    @Test
    @DisplayName("PATCH /api/incidents/{id}/assign should return 400 for empty assignee")
    void assignIncident_EmptyAssignee() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("assignedTo", ""))
        .when()
            .patch("/api/incidents/test-id/assign")
        .then()
            .statusCode(400);

        verify(securityIncidentService, never()).assignTo(any(), any());
    }

    @Test
    @DisplayName("PATCH /api/incidents/{id}/assign should return 404 for non-existent incident")
    void assignIncident_NotFound() {
        when(securityIncidentService.assignTo("unknown-id", "analyst@company.com")).thenReturn(Uni.createFrom().nullItem());

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("assignedTo", "analyst@company.com"))
        .when()
            .patch("/api/incidents/unknown-id/assign")
        .then()
            .statusCode(404);

        verify(securityIncidentService).assignTo("unknown-id", "analyst@company.com");
    }

    @Test
    @DisplayName("POST /api/incidents/{id}/resolve should resolve incident with resolution")
    void resolveIncident_WithResolution() {
        when(securityIncidentService.resolve("test-id", "Issue fixed")).thenReturn(Uni.createFrom().item(testIncident));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("resolution", "Issue fixed"))
        .when()
            .post("/api/incidents/test-id/resolve")
        .then()
            .statusCode(200);

        verify(securityIncidentService).resolve("test-id", "Issue fixed");
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("POST /api/incidents/{id}/resolve should resolve incident without resolution")
    void resolveIncident_WithoutResolution() {
        when(securityIncidentService.resolve("test-id", null)).thenReturn(Uni.createFrom().item(testIncident));
        when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of())
        .when()
            .post("/api/incidents/test-id/resolve")
        .then()
            .statusCode(200);

        verify(securityIncidentService).resolve("test-id", null);
        verify(securityIncidentMapper).toDto(testIncident);
    }

    @Test
    @DisplayName("POST /api/incidents/{id}/resolve should return 404 for non-existent incident")
    void resolveIncident_NotFound() {
        when(securityIncidentService.resolve("unknown-id", "Issue fixed")).thenReturn(Uni.createFrom().nullItem());

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("resolution", "Issue fixed"))
        .when()
            .post("/api/incidents/unknown-id/resolve")
        .then()
            .statusCode(404);

        verify(securityIncidentService).resolve("unknown-id", "Issue fixed");
    }

    @Test
    @DisplayName("DELETE /api/incidents/{id} should delete incident")
    void deleteIncident() {
        when(securityIncidentService.delete("test-id")).thenReturn(Uni.createFrom().item(true));

        given()
        .when()
            .delete("/api/incidents/test-id")
        .then()
            .statusCode(204);

        verify(securityIncidentService).delete("test-id");
    }
}