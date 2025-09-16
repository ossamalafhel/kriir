package org.cyberisk.platform.controller;

import org.cyberisk.platform.dto.SecurityIncidentDto;
import org.cyberisk.platform.dto.CreateSecurityIncidentRequest;
import org.cyberisk.platform.mapper.SecurityIncidentMapper;
import org.cyberisk.platform.model.SecurityIncident;
import org.cyberisk.platform.service.SecurityIncidentService;
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
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(SecurityIncidentController.class)
@DisplayName("SecurityIncidentController Tests")
class SecurityIncidentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SecurityIncidentService securityIncidentService;

    @MockBean
    private SecurityIncidentMapper securityIncidentMapper;

    private SecurityIncident testIncident;
    private SecurityIncidentDto testIncidentDto;
    private CreateSecurityIncidentRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        testIncident = new SecurityIncident("Malware Detection", "MALWARE_DETECTION", "HIGH", 1.0, 2.0);
        testIncident.setId("test-id");
        testIncident.setStatus("OPEN");
        testIncident.setDescription("Test description");
        testIncident.setDetectedAt(LocalDateTime.now());

        testIncidentDto = new SecurityIncidentDto("test-id", "Malware Detection", "MALWARE_DETECTION", 
            "HIGH", "OPEN", 1.0, 2.0, "Test description", null, LocalDateTime.now(), null, null);
        
        testCreateRequest = new CreateSecurityIncidentRequest("Malware Detection", "MALWARE_DETECTION", 
            "HIGH", 1.0, 2.0, "Test description", null);
    }

    @Nested
    @DisplayName("GET Endpoints")
    class GetEndpoints {

        @Test
        @DisplayName("GET /api/incidents should return all incidents")
        void getAllIncidents() {
            when(securityIncidentService.findAll()).thenReturn(Flux.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SecurityIncidentDto.class)
                .hasSize(1);

            verify(securityIncidentService).findAll();
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/{id} should return incident when found")
        void getIncidentById_Found() {
            when(securityIncidentService.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents/test-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SecurityIncidentDto.class);

            verify(securityIncidentService).findById("test-id");
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/{id} should return 404 when not found")
        void getIncidentById_NotFound() {
            when(securityIncidentService.findById("unknown-id")).thenReturn(Mono.empty());

            webTestClient.get()
                .uri("/api/incidents/unknown-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

            verify(securityIncidentService).findById("unknown-id");
            verify(securityIncidentMapper, never()).toDto(any());
        }

        @Test
        @DisplayName("GET /api/incidents/status/{status} should return incidents by status")
        void getIncidentsByStatus() {
            when(securityIncidentService.findByStatus("OPEN")).thenReturn(Flux.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents/status/OPEN")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SecurityIncidentDto.class)
                .hasSize(1);

            verify(securityIncidentService).findByStatus("OPEN");
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/severity/{severity} should return incidents by severity")
        void getIncidentsBySeverity() {
            when(securityIncidentService.findBySeverity("HIGH")).thenReturn(Flux.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents/severity/HIGH")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SecurityIncidentDto.class)
                .hasSize(1);

            verify(securityIncidentService).findBySeverity("HIGH");
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/type/{type} should return incidents by type")
        void getIncidentsByType() {
            when(securityIncidentService.findByType("MALWARE_DETECTION")).thenReturn(Flux.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents/type/MALWARE_DETECTION")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SecurityIncidentDto.class)
                .hasSize(1);

            verify(securityIncidentService).findByType("MALWARE_DETECTION");
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/asset/{assetId} should return incidents by asset")
        void getIncidentsByAsset() {
            when(securityIncidentService.findByAffectedAssetId("asset-123")).thenReturn(Flux.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents/asset/asset-123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SecurityIncidentDto.class)
                .hasSize(1);

            verify(securityIncidentService).findByAffectedAssetId("asset-123");
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/location should return incidents by location bounds")
        void getIncidentsByLocation() {
            when(securityIncidentService.findByLocationBounds(0.0, 2.0, 1.0, 3.0)).thenReturn(Flux.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents/location?minX=0.0&maxX=2.0&minY=1.0&maxY=3.0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SecurityIncidentDto.class)
                .hasSize(1);

            verify(securityIncidentService).findByLocationBounds(0.0, 2.0, 1.0, 3.0);
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/open should return open incidents")
        void getOpenIncidents() {
            when(securityIncidentService.findOpenIncidents()).thenReturn(Flux.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents/open")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SecurityIncidentDto.class)
                .hasSize(1);

            verify(securityIncidentService).findOpenIncidents();
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/critical should return critical incidents")
        void getCriticalIncidents() {
            when(securityIncidentService.findCriticalIncidents()).thenReturn(Flux.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.get()
                .uri("/api/incidents/critical")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SecurityIncidentDto.class)
                .hasSize(1);

            verify(securityIncidentService).findCriticalIncidents();
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("GET /api/incidents/count should return incident count")
        void getIncidentCount() {
            when(securityIncidentService.count()).thenReturn(Mono.just(15L));

            webTestClient.get()
                .uri("/api/incidents/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(15);

            verify(securityIncidentService).count();
        }

        @Test
        @DisplayName("GET /api/incidents/count/status/{status} should return incident count by status")
        void getIncidentCountByStatus() {
            when(securityIncidentService.countByStatus("OPEN")).thenReturn(Mono.just(8L));

            webTestClient.get()
                .uri("/api/incidents/count/status/OPEN")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(8);

            verify(securityIncidentService).countByStatus("OPEN");
        }

        @Test
        @DisplayName("GET /api/incidents/count/severity/{severity} should return incident count by severity")
        void getIncidentCountBySeverity() {
            when(securityIncidentService.countBySeverity("HIGH")).thenReturn(Mono.just(3L));

            webTestClient.get()
                .uri("/api/incidents/count/severity/HIGH")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(3);

            verify(securityIncidentService).countBySeverity("HIGH");
        }
    }

    @Nested
    @DisplayName("POST Endpoints")
    class PostEndpoints {

        @Test
        @DisplayName("POST /api/incidents should create new incident")
        void createIncident() {
            when(securityIncidentMapper.toEntity(testCreateRequest)).thenReturn(testIncident);
            when(securityIncidentService.save(testIncident)).thenReturn(Mono.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.post()
                .uri("/api/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testCreateRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SecurityIncidentDto.class);

            verify(securityIncidentMapper).toEntity(testCreateRequest);
            verify(securityIncidentService).save(testIncident);
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("POST /api/incidents should return 400 for invalid request")
        void createIncident_InvalidRequest() {
            CreateSecurityIncidentRequest invalidRequest = new CreateSecurityIncidentRequest("", "", "", null, null, null, null);

            webTestClient.post()
                .uri("/api/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();

            verify(securityIncidentMapper, never()).toEntity(any());
            verify(securityIncidentService, never()).save(any());
        }
    }

    @Nested
    @DisplayName("PUT Endpoints")
    class PutEndpoints {

        @Test
        @DisplayName("PUT /api/incidents/{id} should update existing incident")
        void updateIncident_Found() {
            when(securityIncidentService.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentService.save(testIncident)).thenReturn(Mono.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.put()
                .uri("/api/incidents/test-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testCreateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SecurityIncidentDto.class);

            verify(securityIncidentService).findById("test-id");
            verify(securityIncidentMapper).updateEntity(testIncident, testCreateRequest);
            verify(securityIncidentService).save(testIncident);
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("PUT /api/incidents/{id} should return 404 for non-existent incident")
        void updateIncident_NotFound() {
            when(securityIncidentService.findById("unknown-id")).thenReturn(Mono.empty());

            webTestClient.put()
                .uri("/api/incidents/unknown-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testCreateRequest)
                .exchange()
                .expectStatus().isNotFound();

            verify(securityIncidentService).findById("unknown-id");
            verify(securityIncidentMapper, never()).updateEntity(any(), any());
            verify(securityIncidentService, never()).save(any());
        }
    }

    @Nested
    @DisplayName("PATCH Endpoints")
    class PatchEndpoints {

        @Test
        @DisplayName("PATCH /api/incidents/{id}/status should update incident status")
        void updateIncidentStatus_Success() {
            when(securityIncidentService.updateStatus("test-id", "RESOLVED")).thenReturn(Mono.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.patch()
                .uri("/api/incidents/test-id/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status", "RESOLVED"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SecurityIncidentDto.class);

            verify(securityIncidentService).updateStatus("test-id", "RESOLVED");
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("PATCH /api/incidents/{id}/status should return 400 for null status")
        void updateIncidentStatus_NullStatus() {
            webTestClient.patch()
                .uri("/api/incidents/test-id/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("other", "value"))
                .exchange()
                .expectStatus().isBadRequest();

            verify(securityIncidentService, never()).updateStatus(any(), any());
        }

        @Test
        @DisplayName("PATCH /api/incidents/{id}/status should return 400 for empty status")
        void updateIncidentStatus_EmptyStatus() {
            webTestClient.patch()
                .uri("/api/incidents/test-id/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status", ""))
                .exchange()
                .expectStatus().isBadRequest();

            verify(securityIncidentService, never()).updateStatus(any(), any());
        }

        @Test
        @DisplayName("PATCH /api/incidents/{id}/status should return 404 for non-existent incident")
        void updateIncidentStatus_NotFound() {
            when(securityIncidentService.updateStatus("unknown-id", "RESOLVED")).thenReturn(Mono.empty());

            webTestClient.patch()
                .uri("/api/incidents/unknown-id/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status", "RESOLVED"))
                .exchange()
                .expectStatus().isNotFound();

            verify(securityIncidentService).updateStatus("unknown-id", "RESOLVED");
        }

        @Test
        @DisplayName("PATCH /api/incidents/{id}/assign should assign incident")
        void assignIncident_Success() {
            when(securityIncidentService.assignTo("test-id", "analyst@company.com")).thenReturn(Mono.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.patch()
                .uri("/api/incidents/test-id/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("assignedTo", "analyst@company.com"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SecurityIncidentDto.class);

            verify(securityIncidentService).assignTo("test-id", "analyst@company.com");
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("PATCH /api/incidents/{id}/assign should return 400 for null assignee")
        void assignIncident_NullAssignee() {
            webTestClient.patch()
                .uri("/api/incidents/test-id/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("other", "value"))
                .exchange()
                .expectStatus().isBadRequest();

            verify(securityIncidentService, never()).assignTo(any(), any());
        }

        @Test
        @DisplayName("PATCH /api/incidents/{id}/assign should return 400 for empty assignee")
        void assignIncident_EmptyAssignee() {
            webTestClient.patch()
                .uri("/api/incidents/test-id/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("assignedTo", ""))
                .exchange()
                .expectStatus().isBadRequest();

            verify(securityIncidentService, never()).assignTo(any(), any());
        }

        @Test
        @DisplayName("PATCH /api/incidents/{id}/assign should return 404 for non-existent incident")
        void assignIncident_NotFound() {
            when(securityIncidentService.assignTo("unknown-id", "analyst@company.com")).thenReturn(Mono.empty());

            webTestClient.patch()
                .uri("/api/incidents/unknown-id/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("assignedTo", "analyst@company.com"))
                .exchange()
                .expectStatus().isNotFound();

            verify(securityIncidentService).assignTo("unknown-id", "analyst@company.com");
        }
    }

    @Nested
    @DisplayName("Resolution Endpoint")
    class ResolutionEndpoint {

        @Test
        @DisplayName("POST /api/incidents/{id}/resolve should resolve incident with resolution")
        void resolveIncident_WithResolution() {
            when(securityIncidentService.resolve("test-id", "Issue fixed")).thenReturn(Mono.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.post()
                .uri("/api/incidents/test-id/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("resolution", "Issue fixed"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SecurityIncidentDto.class);

            verify(securityIncidentService).resolve("test-id", "Issue fixed");
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("POST /api/incidents/{id}/resolve should resolve incident without resolution")
        void resolveIncident_WithoutResolution() {
            when(securityIncidentService.resolve("test-id", null)).thenReturn(Mono.just(testIncident));
            when(securityIncidentMapper.toDto(testIncident)).thenReturn(testIncidentDto);

            webTestClient.post()
                .uri("/api/incidents/test-id/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of())
                .exchange()
                .expectStatus().isOk()
                .expectBody(SecurityIncidentDto.class);

            verify(securityIncidentService).resolve("test-id", null);
            verify(securityIncidentMapper).toDto(testIncident);
        }

        @Test
        @DisplayName("POST /api/incidents/{id}/resolve should return 404 for non-existent incident")
        void resolveIncident_NotFound() {
            when(securityIncidentService.resolve("unknown-id", "Issue fixed")).thenReturn(Mono.empty());

            webTestClient.post()
                .uri("/api/incidents/unknown-id/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("resolution", "Issue fixed"))
                .exchange()
                .expectStatus().isNotFound();

            verify(securityIncidentService).resolve("unknown-id", "Issue fixed");
        }
    }

    @Nested
    @DisplayName("DELETE Endpoints")  
    class DeleteEndpoints {

        @Test
        @DisplayName("DELETE /api/incidents/{id} should delete incident")
        void deleteIncident() {
            when(securityIncidentService.deleteById("test-id")).thenReturn(Mono.empty());

            webTestClient.delete()
                .uri("/api/incidents/test-id")
                .exchange()
                .expectStatus().isNoContent();

            verify(securityIncidentService).deleteById("test-id");
        }
    }
}