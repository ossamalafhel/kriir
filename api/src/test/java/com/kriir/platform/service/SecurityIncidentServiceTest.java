package com.kriir.platform.service;

import com.kriir.platform.model.SecurityIncident;
import com.kriir.platform.repository.SecurityIncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityIncidentService Tests")
class SecurityIncidentServiceTest {

    @Mock
    private SecurityIncidentRepository securityIncidentRepository;

    private SecurityIncidentService securityIncidentService;

    private SecurityIncident testIncident;

    @BeforeEach
    void setUp() {
        securityIncidentService = new SecurityIncidentService(securityIncidentRepository);
        
        testIncident = new SecurityIncident("Test Incident", "MALWARE_DETECTION", "HIGH", 2.3522, 48.8566);
        testIncident.setId("test-id");
        testIncident.setDescription("Test description");
    }

    @Nested
    @DisplayName("Read Operations")
    class ReadOperations {

        @Test
        @DisplayName("Should find all incidents")
        void findAll() {
            SecurityIncident incident1 = new SecurityIncident("Incident 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident incident2 = new SecurityIncident("Incident 2", "UNAUTHORIZED_ACCESS", "CRITICAL", 2.0, 2.0);
            
            when(securityIncidentRepository.findAll()).thenReturn(Flux.just(incident1, incident2));

            StepVerifier.create(securityIncidentService.findAll())
                .expectNext(incident1)
                .expectNext(incident2)
                .verifyComplete();

            verify(securityIncidentRepository).findAll();
        }

        @Test
        @DisplayName("Should find incident by id")
        void findById() {
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));

            StepVerifier.create(securityIncidentService.findById("test-id"))
                .expectNext(testIncident)
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
        }

        @Test
        @DisplayName("Should return empty when incident not found")
        void findByIdNotFound() {
            when(securityIncidentRepository.findById("unknown-id")).thenReturn(Mono.empty());

            StepVerifier.create(securityIncidentService.findById("unknown-id"))
                .verifyComplete();

            verify(securityIncidentRepository).findById("unknown-id");
        }

        @Test
        @DisplayName("Should find incidents by status")
        void findByStatus() {
            SecurityIncident open1 = new SecurityIncident("Open 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident open2 = new SecurityIncident("Open 2", "DATA_BREACH", "CRITICAL", 2.0, 2.0);
            
            when(securityIncidentRepository.findByStatus("OPEN")).thenReturn(Flux.just(open1, open2));

            StepVerifier.create(securityIncidentService.findByStatus("OPEN"))
                .expectNext(open1)
                .expectNext(open2)
                .verifyComplete();

            verify(securityIncidentRepository).findByStatus("OPEN");
        }

        @Test
        @DisplayName("Should find incidents by severity")
        void findBySeverity() {
            SecurityIncident critical1 = new SecurityIncident("Critical 1", "RANSOMWARE", "CRITICAL", 1.0, 1.0);
            SecurityIncident critical2 = new SecurityIncident("Critical 2", "DATA_BREACH", "CRITICAL", 2.0, 2.0);
            
            when(securityIncidentRepository.findBySeverity("CRITICAL"))
                .thenReturn(Flux.just(critical1, critical2));

            StepVerifier.create(securityIncidentService.findBySeverity("CRITICAL"))
                .expectNext(critical1)
                .expectNext(critical2)
                .verifyComplete();

            verify(securityIncidentRepository).findBySeverity("CRITICAL");
        }

        @Test
        @DisplayName("Should find incidents by type")
        void findByType() {
            SecurityIncident ddos1 = new SecurityIncident("DDoS 1", "DDoS_ATTACK", "HIGH", 1.0, 1.0);
            SecurityIncident ddos2 = new SecurityIncident("DDoS 2", "DDoS_ATTACK", "CRITICAL", 2.0, 2.0);
            
            when(securityIncidentRepository.findByType("DDoS_ATTACK"))
                .thenReturn(Flux.just(ddos1, ddos2));

            StepVerifier.create(securityIncidentService.findByType("DDoS_ATTACK"))
                .expectNext(ddos1)
                .expectNext(ddos2)
                .verifyComplete();

            verify(securityIncidentRepository).findByType("DDoS_ATTACK");
        }

        @Test
        @DisplayName("Should find incidents by affected asset")
        void findByAffectedAssetId() {
            SecurityIncident incident = new SecurityIncident("Asset Incident", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            incident.setAffectedAssetId("asset-123");
            
            when(securityIncidentRepository.findByAffectedAssetId("asset-123"))
                .thenReturn(Flux.just(incident));

            StepVerifier.create(securityIncidentService.findByAffectedAssetId("asset-123"))
                .expectNext(incident)
                .verifyComplete();

            verify(securityIncidentRepository).findByAffectedAssetId("asset-123");
        }

        @Test
        @DisplayName("Should find incidents by location bounds")
        void findByLocationBounds() {
            SecurityIncident incident1 = new SecurityIncident("Incident 1", "MALWARE_DETECTION", "HIGH", 1.5, 1.5);
            SecurityIncident incident2 = new SecurityIncident("Incident 2", "PHISHING_ATTACK", "MEDIUM", 2.5, 2.5);
            
            when(securityIncidentRepository.findByLocationBounds(1.0, 3.0, 1.0, 3.0))
                .thenReturn(Flux.just(incident1, incident2));

            StepVerifier.create(securityIncidentService.findByLocationBounds(1.0, 3.0, 1.0, 3.0))
                .expectNext(incident1)
                .expectNext(incident2)
                .verifyComplete();

            verify(securityIncidentRepository).findByLocationBounds(1.0, 3.0, 1.0, 3.0);
        }

        @Test
        @DisplayName("Should find open incidents")
        void findOpenIncidents() {
            SecurityIncident open1 = new SecurityIncident("Open 1", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            SecurityIncident open2 = new SecurityIncident("Open 2", "DATA_BREACH", "CRITICAL", 2.0, 2.0);
            
            when(securityIncidentRepository.findByStatus("OPEN")).thenReturn(Flux.just(open1, open2));

            StepVerifier.create(securityIncidentService.findOpenIncidents())
                .expectNext(open1)
                .expectNext(open2)
                .verifyComplete();

            verify(securityIncidentRepository).findByStatus("OPEN");
        }

        @Test
        @DisplayName("Should find critical incidents")
        void findCriticalIncidents() {
            SecurityIncident critical1 = new SecurityIncident("Critical 1", "RANSOMWARE", "CRITICAL", 1.0, 1.0);
            
            when(securityIncidentRepository.findBySeverity("CRITICAL"))
                .thenReturn(Flux.just(critical1));

            StepVerifier.create(securityIncidentService.findCriticalIncidents())
                .expectNext(critical1)
                .verifyComplete();

            verify(securityIncidentRepository).findBySeverity("CRITICAL");
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperations {

        @Test
        @DisplayName("Should save incident with detectedAt timestamp")
        void saveIncidentWithTimestamp() {
            SecurityIncident newIncident = new SecurityIncident("New Incident", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            newIncident.setDetectedAt(null);
            
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                SecurityIncident saved = invocation.getArgument(0);
                return Mono.just(saved);
            });

            StepVerifier.create(securityIncidentService.save(newIncident))
                .assertNext(saved -> {
                    assertThat(saved.getDetectedAt()).isNotNull();
                    assertThat(saved.getTitle()).isEqualTo("New Incident");
                })
                .verifyComplete();

            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should preserve existing detectedAt if already set")
        void saveIncidentPreserveTimestamp() {
            LocalDateTime existingTime = LocalDateTime.now().minusHours(1);
            SecurityIncident newIncident = new SecurityIncident("New Incident", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            newIncident.setDetectedAt(existingTime);
            
            when(securityIncidentRepository.save(newIncident)).thenReturn(Mono.just(newIncident));

            StepVerifier.create(securityIncidentService.save(newIncident))
                .assertNext(saved -> {
                    assertThat(saved.getDetectedAt()).isEqualTo(existingTime);
                })
                .verifyComplete();

            verify(securityIncidentRepository).save(newIncident);
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should update existing incident")
        void updateExistingIncident() {
            SecurityIncident updatedIncident = new SecurityIncident("Updated Incident", "DATA_BREACH", "CRITICAL", 3.0, 4.0);
            updatedIncident.setDescription("Updated description");
            
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.update("test-id", updatedIncident))
                .assertNext(result -> {
                    assertThat(result.getId()).isEqualTo("test-id");
                    assertThat(result.getTitle()).isEqualTo("Updated Incident");
                    assertThat(result.getType()).isEqualTo("DATA_BREACH");
                    assertThat(result.getDetectedAt()).isNotNull(); // Just check it's not null since it should preserve original
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should return empty when updating non-existent incident")
        void updateNonExistentIncident() {
            SecurityIncident updatedIncident = new SecurityIncident("Updated", "MALWARE_DETECTION", "HIGH", 1.0, 1.0);
            
            when(securityIncidentRepository.findById("unknown-id")).thenReturn(Mono.empty());

            StepVerifier.create(securityIncidentService.update("unknown-id", updatedIncident))
                .verifyComplete();

            verify(securityIncidentRepository).findById("unknown-id");
            verify(securityIncidentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update incident status to RESOLVED")
        void updateStatusToResolved() {
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.updateStatus("test-id", "RESOLVED"))
                .assertNext(result -> {
                    assertThat(result.getStatus()).isEqualTo("RESOLVED");
                    assertThat(result.getResolvedAt()).isNotNull();
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should update incident status to CLOSED")
        void updateStatusToClosed() {
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.updateStatus("test-id", "CLOSED"))
                .assertNext(result -> {
                    assertThat(result.getStatus()).isEqualTo("CLOSED");
                    assertThat(result.getResolvedAt()).isNotNull();
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should update incident status without setting resolvedAt for other statuses")
        void updateStatusInProgress() {
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.updateStatus("test-id", "IN_PROGRESS"))
                .assertNext(result -> {
                    assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
                    assertThat(result.getResolvedAt()).isNull();
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should assign incident to user")
        void assignTo() {
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.assignTo("test-id", "analyst@company.com"))
                .assertNext(result -> {
                    assertThat(result.getAssignedTo()).isEqualTo("analyst@company.com");
                    assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should not change status if already IN_PROGRESS when assigning")
        void assignToAlreadyInProgress() {
            testIncident.setStatus("IN_PROGRESS");
            
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.assignTo("test-id", "analyst@company.com"))
                .assertNext(result -> {
                    assertThat(result.getAssignedTo()).isEqualTo("analyst@company.com");
                    assertThat(result.getStatus()).isEqualTo("IN_PROGRESS");
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should resolve incident with resolution")
        void resolveWithResolution() {
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.resolve("test-id", "Issue has been fixed"))
                .assertNext(result -> {
                    assertThat(result.getStatus()).isEqualTo("RESOLVED");
                    assertThat(result.getResolvedAt()).isNotNull();
                    assertThat(result.getDescription()).contains("Resolution: Issue has been fixed");
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should resolve incident without resolution")
        void resolveWithoutResolution() {
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.resolve("test-id", null))
                .assertNext(result -> {
                    assertThat(result.getStatus()).isEqualTo("RESOLVED");
                    assertThat(result.getResolvedAt()).isNotNull();
                    assertThat(result.getDescription()).isEqualTo("Test description");
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should resolve incident with empty resolution")
        void resolveWithEmptyResolution() {
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.resolve("test-id", ""))
                .assertNext(result -> {
                    assertThat(result.getStatus()).isEqualTo("RESOLVED");
                    assertThat(result.getResolvedAt()).isNotNull();
                    assertThat(result.getDescription()).isEqualTo("Test description");
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }

        @Test
        @DisplayName("Should handle resolve with null description")
        void resolveWithNullDescription() {
            testIncident.setDescription(null);
            
            when(securityIncidentRepository.findById("test-id")).thenReturn(Mono.just(testIncident));
            when(securityIncidentRepository.save(any(SecurityIncident.class))).thenAnswer(invocation -> {
                return Mono.just(invocation.getArgument(0));
            });

            StepVerifier.create(securityIncidentService.resolve("test-id", "Fixed"))
                .assertNext(result -> {
                    assertThat(result.getDescription()).isEqualTo("Resolution: Fixed");
                })
                .verifyComplete();

            verify(securityIncidentRepository).findById("test-id");
            verify(securityIncidentRepository).save(any(SecurityIncident.class));
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("Should delete incident by id")
        void deleteById() {
            when(securityIncidentRepository.deleteById("test-id")).thenReturn(Mono.empty());

            StepVerifier.create(securityIncidentService.deleteById("test-id"))
                .verifyComplete();

            verify(securityIncidentRepository).deleteById("test-id");
        }
    }

    @Nested
    @DisplayName("Count Operations")
    class CountOperations {

        @Test
        @DisplayName("Should count all incidents")
        void countAll() {
            when(securityIncidentRepository.count()).thenReturn(Mono.just(25L));

            StepVerifier.create(securityIncidentService.count())
                .expectNext(25L)
                .verifyComplete();

            verify(securityIncidentRepository).count();
        }

        @Test
        @DisplayName("Should count incidents by status")
        void countByStatus() {
            when(securityIncidentRepository.countByStatus("OPEN")).thenReturn(Mono.just(15L));

            StepVerifier.create(securityIncidentService.countByStatus("OPEN"))
                .expectNext(15L)
                .verifyComplete();

            verify(securityIncidentRepository).countByStatus("OPEN");
        }

        @Test
        @DisplayName("Should count incidents by severity")
        void countBySeverity() {
            when(securityIncidentRepository.countBySeverity("CRITICAL")).thenReturn(Mono.just(5L));

            StepVerifier.create(securityIncidentService.countBySeverity("CRITICAL"))
                .expectNext(5L)
                .verifyComplete();

            verify(securityIncidentRepository).countBySeverity("CRITICAL");
        }
    }
}