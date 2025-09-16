package com.kriir.platform.repository;

import com.kriir.platform.model.Asset;
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
@DisplayName("AssetRepository Tests")
class AssetRepositoryTest {

    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    void cleanDatabase() {
        assetRepository.deleteAll().block();
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperations {

        @Test
        @DisplayName("Should save and find asset by id")
        void saveAndFindById() {
            Asset asset = new Asset("Test Server", "SERVER", "HIGH", 2.3522, 48.8566);

            StepVerifier.create(assetRepository.save(asset))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getName()).isEqualTo("Test Server");
                    assertThat(saved.getType()).isEqualTo("SERVER");
                    assertThat(saved.getCriticality()).isEqualTo("HIGH");
                    assertThat(saved.getStatus()).isEqualTo("ACTIVE");
                    assertThat(saved.getLastSeen()).isNotNull();
                })
                .verifyComplete();

            StepVerifier.create(assetRepository.findById(asset.getId()))
                .assertNext(found -> {
                    assertThat(found.getName()).isEqualTo("Test Server");
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Should update existing asset")
        void updateExistingAsset() {
            Asset asset = new Asset("Original", "SERVER", "HIGH", 1.0, 1.0);
            Asset saved = assetRepository.save(asset).block();

            saved.setName("Updated");
            saved.setCriticality("CRITICAL");

            StepVerifier.create(assetRepository.save(saved))
                .assertNext(updated -> {
                    assertThat(updated.getName()).isEqualTo("Updated");
                    assertThat(updated.getCriticality()).isEqualTo("CRITICAL");
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Should delete asset by id")
        void deleteById() {
            Asset asset = new Asset("To Delete", "SERVER", "LOW", 1.0, 1.0);
            String id = assetRepository.save(asset).block().getId();

            StepVerifier.create(assetRepository.deleteById(id))
                .verifyComplete();

            StepVerifier.create(assetRepository.findById(id))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find all assets")
        void findAll() {
            Asset asset1 = new Asset("Asset 1", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Asset 2", "DATABASE", "CRITICAL", 2.0, 2.0);
            Asset asset3 = new Asset("Asset 3", "FIREWALL", "MEDIUM", 3.0, 3.0);

            assetRepository.saveAll(Arrays.asList(asset1, asset2, asset3)).blockLast();

            StepVerifier.create(assetRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Query Operations")
    class QueryOperations {

        @Test
        @DisplayName("Should find assets by type")
        void findByType() {
            Asset server1 = new Asset("Server 1", "SERVER", "HIGH", 1.0, 1.0);
            Asset server2 = new Asset("Server 2", "SERVER", "MEDIUM", 2.0, 2.0);
            Asset database = new Asset("Database", "DATABASE", "CRITICAL", 3.0, 3.0);

            assetRepository.saveAll(Arrays.asList(server1, server2, database)).blockLast();

            StepVerifier.create(assetRepository.findByType("SERVER"))
                .assertNext(asset -> assertThat(asset.getType()).isEqualTo("SERVER"))
                .assertNext(asset -> assertThat(asset.getType()).isEqualTo("SERVER"))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find assets by criticality")
        void findByCriticality() {
            Asset critical1 = new Asset("Critical 1", "SERVER", "CRITICAL", 1.0, 1.0);
            Asset critical2 = new Asset("Critical 2", "DATABASE", "CRITICAL", 2.0, 2.0);
            Asset high = new Asset("High", "FIREWALL", "HIGH", 3.0, 3.0);

            assetRepository.saveAll(Arrays.asList(critical1, critical2, high)).blockLast();

            StepVerifier.create(assetRepository.findByCriticality("CRITICAL"))
                .assertNext(asset -> assertThat(asset.getCriticality()).isEqualTo("CRITICAL"))
                .assertNext(asset -> assertThat(asset.getCriticality()).isEqualTo("CRITICAL"))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find assets by status")
        void findByStatus() {
            Asset active = new Asset("Active", "SERVER", "HIGH", 1.0, 1.0);
            Asset compromised = new Asset("Compromised", "DATABASE", "CRITICAL", 2.0, 2.0);
            compromised.setStatus("COMPROMISED");
            
            assetRepository.save(active).block();
            assetRepository.save(compromised).block();

            StepVerifier.create(assetRepository.findByStatus("COMPROMISED"))
                .assertNext(asset -> {
                    assertThat(asset.getName()).isEqualTo("Compromised");
                    assertThat(asset.getStatus()).isEqualTo("COMPROMISED");
                })
                .verifyComplete();
        }

        @Test
        @DisplayName("Should find assets by location bounds")
        void findByLocationBounds() {
            Asset inside1 = new Asset("Inside 1", "SERVER", "HIGH", 1.5, 1.5);
            Asset inside2 = new Asset("Inside 2", "DATABASE", "MEDIUM", 2.5, 2.5);
            Asset outside = new Asset("Outside", "FIREWALL", "LOW", 5.0, 5.0);

            assetRepository.saveAll(Arrays.asList(inside1, inside2, outside)).blockLast();

            StepVerifier.create(assetRepository.findByLocationBounds(1.0, 3.0, 1.0, 3.0))
                .assertNext(asset -> assertThat(asset.getName()).isIn("Inside 1", "Inside 2"))
                .assertNext(asset -> assertThat(asset.getName()).isIn("Inside 1", "Inside 2"))
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Count Operations")
    class CountOperations {

        @Test
        @DisplayName("Should count all assets")
        void countAll() {
            Asset asset1 = new Asset("Asset 1", "SERVER", "HIGH", 1.0, 1.0);
            Asset asset2 = new Asset("Asset 2", "DATABASE", "CRITICAL", 2.0, 2.0);
            Asset asset3 = new Asset("Asset 3", "FIREWALL", "MEDIUM", 3.0, 3.0);

            assetRepository.saveAll(Arrays.asList(asset1, asset2, asset3)).blockLast();

            StepVerifier.create(assetRepository.count())
                .expectNext(3L)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should count assets by type")
        void countByType() {
            Asset server1 = new Asset("Server 1", "SERVER", "HIGH", 1.0, 1.0);
            Asset server2 = new Asset("Server 2", "SERVER", "MEDIUM", 2.0, 2.0);
            Asset database = new Asset("Database", "DATABASE", "CRITICAL", 3.0, 3.0);

            assetRepository.saveAll(Arrays.asList(server1, server2, database)).blockLast();

            StepVerifier.create(assetRepository.countByType("SERVER"))
                .expectNext(2L)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should count assets by status")
        void countByStatus() {
            Asset active1 = new Asset("Active 1", "SERVER", "HIGH", 1.0, 1.0);
            Asset active2 = new Asset("Active 2", "DATABASE", "CRITICAL", 2.0, 2.0);
            Asset compromised = new Asset("Compromised", "SERVER", "HIGH", 3.0, 3.0);
            compromised.setStatus("COMPROMISED");

            assetRepository.save(active1).block();
            assetRepository.save(active2).block();
            assetRepository.save(compromised).block();

            StepVerifier.create(assetRepository.countByStatus("ACTIVE"))
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
            StepVerifier.create(assetRepository.findByType("NONEXISTENT"))
                .verifyComplete();

            StepVerifier.create(assetRepository.findByLocationBounds(100.0, 200.0, 100.0, 200.0))
                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle null values in optional fields")
        void handleNullValues() {
            Asset asset = new Asset();
            asset.setName("Minimal Asset");
            asset.setType("SERVER");
            // criticality is null
            asset.setX(1.0);
            asset.setY(1.0);

            StepVerifier.create(assetRepository.save(asset))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getName()).isEqualTo("Minimal Asset");
                    assertThat(saved.getCriticality()).isNull();
                    assertThat(saved.getStatus()).isEqualTo("ACTIVE");
                })
                .verifyComplete();
        }
    }
}