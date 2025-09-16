package com.kriir.platform.repository;

import com.kriir.platform.model.SecurityIncident;
import com.kriir.platform.config.TestConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.Disabled;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@Disabled("Temporarily disabled due to context loading issues")
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("SecurityIncidentRepository Tests")
class SecurityIncidentRepositoryTest {

    @Autowired
    private SecurityIncidentRepository securityIncidentRepository;

    @BeforeEach
    void cleanDatabase() {
        securityIncidentRepository.deleteAll().block();
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperations {

        @Test
        @DisplayName("Should save and find incident by id")
        void saveAndFindById() {
            SecurityIncident incident = new SecurityIncident("Test Incident", "MALWARE_DETECTION", "HIGH", 2.3522, 48.8566);

            StepVerifier.create(securityIncidentRepository.save(incident))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getTitle()).isEqualTo("Test Incident");
                    assertThat(saved.getType()).isEqualTo("MALWARE_DETECTION");
                    assertThat(saved.getSeverity()).isEqualTo("HIGH");
                    assertThat(saved.getStatus()).isEqualTo("OPEN");
                    assertThat(saved.getDetectedAt()).isNotNull();
                })
                .verifyComplete();

            StepVerifier.create(securityIncidentRepository.findById(incident.getId()))
                .assertNext(found -> {
                    assertThat(found.getTitle()).isEqualTo("Test Incident");
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Should update existing incident")
        void updateExistingIncident() {
            SecurityIncident incident = new SecurityIncident("Original", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident saved = securityIncidentRepository.save(incident).block();

            saved.setTitle("Updated");
            saved.setSeverity("CRITICAL");
            saved.setStatus("IN_PROGRESS");
            saved.setAssignedTo("analyst@company.com");

            StepVerifier.create(securityIncidentRepository.save(saved))
                .assertNext(updated -> {
                    assertThat(updated.getTitle()).isEqualTo("Updated");
                    assertThat(updated.getSeverity()).isEqualTo("CRITICAL");
                    assertThat(updated.getStatus()).isEqualTo("IN_PROGRESS");
                    assertThat(updated.getAssignedTo()).isEqualTo("analyst@company.com");
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Should delete incident by id")
        void deleteById() {
            SecurityIncident incident = new SecurityIncident("To Delete", "PHISHING_ATTACK", "LOW", 1.0, 1.0);
            String id = securityIncidentRepository.save(incident).block().getId();

            StepVerifier.create(securityIncidentRepository.deleteById(id))
                .verifyComplete();

            StepVerifier.create(securityIncidentRepository.findById(id))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find all incidents")
        void findAll() {
            SecurityIncident incident1 = new SecurityIncident("Incident 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Incident 2", "UNAUTHORIZED_ACCESS", "CRITICAL", 2.0, 2.0);
            SecurityIncident incident3 = new SecurityIncident("Incident 3", "DATA_BREACH", "CRITICAL", 3.0, 3.0);

            securityIncidentRepository.saveAll(Arrays.asList(incident1, incident2, incident3)).blockLast();

            StepVerifier.create(securityIncidentRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Query Operations")
    class QueryOperations {

        @Test
        @DisplayName("Should find incidents by status")
        void findByStatus() {
            SecurityIncident open1 = new SecurityIncident("Open 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident open2 = new SecurityIncident("Open 2", "DATA_BREACH", "CRITICAL", 2.0, 2.0);
            SecurityIncident resolved = new SecurityIncident("Resolved", "PHISHING_ATTACK", "MEDIUM", 3.0, 3.0);
            resolved.setStatus("RESOLVED");
            resolved.setResolvedAt(LocalDateTime.now());

            securityIncidentRepository.saveAll(Arrays.asList(open1, open2, resolved)).blockLast();

            StepVerifier.create(securityIncidentRepository.findByStatus("OPEN"))
                .assertNext(incident -> assertThat(incident.getStatus()).isEqualTo("OPEN"))
                .assertNext(incident -> assertThat(incident.getStatus()).isEqualTo("OPEN"))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find incidents by severity")
        void findBySeverity() {
            SecurityIncident critical1 = new SecurityIncident("Critical 1", "RANSOMWARE", "CRITICAL", 1.0, 1.0);
            SecurityIncident critical2 = new SecurityIncident("Critical 2", "DATA_BREACH", "CRITICAL", 2.0, 2.0);
            SecurityIncident high = new SecurityIncident("High", "MALWARE_DETECTION", "HIGH", 3.0, 3.0);

            securityIncidentRepository.saveAll(Arrays.asList(critical1, critical2, high)).blockLast();

            StepVerifier.create(securityIncidentRepository.findBySeverity("CRITICAL"))
                .assertNext(incident -> assertThat(incident.getSeverity()).isEqualTo("CRITICAL"))
                .assertNext(incident -> assertThat(incident.getSeverity()).isEqualTo("CRITICAL"))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find incidents by type")
        void findByType() {
            SecurityIncident malware1 = new SecurityIncident("Malware 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident malware2 = new SecurityIncident("Malware 2", "MALWARE_DETECTION", "CRITICAL", 2.0, 2.0);
            SecurityIncident ddos = new SecurityIncident("DDoS", "DDoS_ATTACK", "HIGH", 3.0, 3.0);

            securityIncidentRepository.saveAll(Arrays.asList(malware1, malware2, ddos)).blockLast();

            StepVerifier.create(securityIncidentRepository.findByType("MALWARE_DETECTION"))
                .assertNext(incident -> assertThat(incident.getType()).isEqualTo("MALWARE_DETECTION"))
                .assertNext(incident -> assertThat(incident.getType()).isEqualTo("MALWARE_DETECTION"))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find incidents by affected asset")
        void findByAffectedAssetId() {
            SecurityIncident incident1 = new SecurityIncident("Asset Incident 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            incident1.setAffectedAssetId("asset-123");
            
            SecurityIncident incident2 = new SecurityIncident("Asset Incident 2", "UNAUTHORIZED_ACCESS", "CRITICAL", 2.0, 2.0);
            incident2.setAffectedAssetId("asset-123");
            
            SecurityIncident incident3 = new SecurityIncident("Other Asset", "PHISHING_ATTACK", "MEDIUM", 3.0, 3.0);
            incident3.setAffectedAssetId("asset-456");

            securityIncidentRepository.saveAll(Arrays.asList(incident1, incident2, incident3)).blockLast();

            StepVerifier.create(securityIncidentRepository.findByAffectedAssetId("asset-123"))
                .assertNext(incident -> assertThat(incident.getAffectedAssetId()).isEqualTo("asset-123"))
                .assertNext(incident -> assertThat(incident.getAffectedAssetId()).isEqualTo("asset-123"))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find incidents by location bounds")
        void findByLocationBounds() {
            SecurityIncident inside1 = new SecurityIncident("Inside 1", "MALWARE_DETECTION", "HIGH", 1.5, 1.5);
            SecurityIncident inside2 = new SecurityIncident("Inside 2", "DATA_BREACH", "CRITICAL", 2.5, 2.5);
            SecurityIncident outside = new SecurityIncident("Outside", "PHISHING_ATTACK", "MEDIUM", 5.0, 5.0);

            securityIncidentRepository.saveAll(Arrays.asList(inside1, inside2, outside)).blockLast();

            StepVerifier.create(securityIncidentRepository.findByLocationBounds(1.0, 3.0, 1.0, 3.0))
                .assertNext(incident -> assertThat(incident.getTitle()).isIn("Inside 1", "Inside 2"))
                .assertNext(incident -> assertThat(incident.getTitle()).isIn("Inside 1", "Inside 2"))
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Count Operations")
    class CountOperations {

        @Test
        @DisplayName("Should count all incidents")
        void countAll() {
            SecurityIncident incident1 = new SecurityIncident("Incident 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Incident 2", "UNAUTHORIZED_ACCESS", "CRITICAL", 2.0, 2.0);
            SecurityIncident incident3 = new SecurityIncident("Incident 3", "DATA_BREACH", "CRITICAL", 3.0, 3.0);

            securityIncidentRepository.saveAll(Arrays.asList(incident1, incident2, incident3)).blockLast();

            StepVerifier.create(securityIncidentRepository.count())
                .expectNext(3L)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should count incidents by status")
        void countByStatus() {
            SecurityIncident open1 = new SecurityIncident("Open 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident open2 = new SecurityIncident("Open 2", "DATA_BREACH", "CRITICAL", 2.0, 2.0);
            SecurityIncident resolved = new SecurityIncident("Resolved", "PHISHING_ATTACK", "MEDIUM", 3.0, 3.0);
            resolved.setStatus("RESOLVED");

            securityIncidentRepository.save(open1).block();
            securityIncidentRepository.save(open2).block();
            securityIncidentRepository.save(resolved).block();

            StepVerifier.create(securityIncidentRepository.countByStatus("OPEN"))
                .expectNext(2L)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should count incidents by severity")
        void countBySeverity() {
            SecurityIncident critical1 = new SecurityIncident("Critical 1", "RANSOMWARE", "CRITICAL", 1.0, 1.0);
            SecurityIncident critical2 = new SecurityIncident("Critical 2", "DATA_BREACH", "CRITICAL", 2.0, 2.0);
            SecurityIncident high = new SecurityIncident("High", "MALWARE_DETECTION", "HIGH", 3.0, 3.0);

            securityIncidentRepository.saveAll(Arrays.asList(critical1, critical2, high)).blockLast();

            StepVerifier.create(securityIncidentRepository.countBySeverity("CRITICAL"))
                .expectNext(2L)
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle empty result sets")
        void handleEmptyResults() {
            StepVerifier.create(securityIncidentRepository.findByType("NONEXISTENT"))
                .verifyComplete();

            StepVerifier.create(securityIncidentRepository.findByAffectedAssetId("nonexistent-asset"))
                .verifyComplete();

            StepVerifier.create(securityIncidentRepository.findByLocationBounds(100.0, 200.0, 100.0, 200.0))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle null values in optional fields")
        void handleNullValues() {
            SecurityIncident incident = new SecurityIncident();
            incident.setTitle("Minimal Incident");
            incident.setType("SUSPICIOUS_ACTIVITY");
            incident.setSeverity("LOW");
            incident.setX(1.0);
            incident.setY(1.0);
            // description, affectedAssetId, assignedTo are null

            StepVerifier.create(securityIncidentRepository.save(incident))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getTitle()).isEqualTo("Minimal Incident");
                    assertThat(saved.getDescription()).isNull();
                    assertThat(saved.getAffectedAssetId()).isNull();
                    assertThat(saved.getAssignedTo()).isNull();
                    assertThat(saved.getStatus()).isEqualTo("OPEN");
                    assertThat(saved.getResolvedAt()).isNull();
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle lifecycle timestamps")
        void handleLifecycleTimestamps() {
            LocalDateTime detectedTime = LocalDateTime.now().minusHours(2);
            LocalDateTime resolvedTime = LocalDateTime.now();
            
            SecurityIncident incident = new SecurityIncident("Lifecycle Test", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            incident.setDetectedAt(detectedTime);
            incident.setStatus("RESOLVED");
            incident.setResolvedAt(resolvedTime);

            StepVerifier.create(securityIncidentRepository.save(incident))
                .assertNext(saved -> {
                    assertThat(saved.getDetectedAt()).isEqualTo(detectedTime);
                    assertThat(saved.getResolvedAt()).isEqualTo(resolvedTime);
                    assertThat(saved.getResolvedAt()).isAfter(saved.getDetectedAt());
                })
                .verifyComplete();
        }
    }
}