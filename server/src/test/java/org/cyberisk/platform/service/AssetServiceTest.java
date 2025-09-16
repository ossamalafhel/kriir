package org.cyberisk.platform.service;

import org.cyberisk.platform.model.Asset;
import org.cyberisk.platform.repository.AssetRepository;
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
@DisplayName("AssetService Tests")
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    private AssetService assetService;

    @BeforeEach
    void setUp() {
        assetService = new AssetService(assetRepository);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("Should find all assets")
        void findAll() {
            Asset asset1 = new Asset("Server1", "SERVER", "HIGH", 1.0, 2.0);
            Asset asset2 = new Asset("Server2", "SERVER", "LOW", 3.0, 4.0);
            when(assetRepository.findAll()).thenReturn(Flux.just(asset1, asset2));

            StepVerifier.create(assetService.findAll())
                .expectNext(asset1)
                .expectNext(asset2)
                .verifyComplete();

            verify(assetRepository).findAll();
        }

        @Test
        @DisplayName("Should find asset by id")
        void findById() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
            asset.setId("test-id");
            when(assetRepository.findById("test-id")).thenReturn(Mono.just(asset));

            StepVerifier.create(assetService.findById("test-id"))
                .expectNext(asset)
                .verifyComplete();

            verify(assetRepository).findById("test-id");
        }

        @Test
        @DisplayName("Should find assets by type")
        void findByType() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
            when(assetRepository.findByType("SERVER")).thenReturn(Flux.just(asset));

            StepVerifier.create(assetService.findByType("SERVER"))
                .expectNext(asset)
                .verifyComplete();

            verify(assetRepository).findByType("SERVER");
        }

        @Test
        @DisplayName("Should find assets by criticality")
        void findByCriticality() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
            when(assetRepository.findByCriticality("HIGH")).thenReturn(Flux.just(asset));

            StepVerifier.create(assetService.findByCriticality("HIGH"))
                .expectNext(asset)
                .verifyComplete();

            verify(assetRepository).findByCriticality("HIGH");
        }

        @Test
        @DisplayName("Should find assets by status")
        void findByStatus() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
            when(assetRepository.findByStatus("ACTIVE")).thenReturn(Flux.just(asset));

            StepVerifier.create(assetService.findByStatus("ACTIVE"))
                .expectNext(asset)
                .verifyComplete();

            verify(assetRepository).findByStatus("ACTIVE");
        }

        @Test
        @DisplayName("Should find assets by location bounds")
        void findByLocationBounds() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.5, 2.5);
            when(assetRepository.findByLocationBounds(1.0, 2.0, 2.0, 3.0))
                .thenReturn(Flux.just(asset));

            StepVerifier.create(assetService.findByLocationBounds(1.0, 2.0, 2.0, 3.0))
                .expectNext(asset)
                .verifyComplete();

            verify(assetRepository).findByLocationBounds(1.0, 2.0, 2.0, 3.0);
        }
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveOperations {

        @Test
        @DisplayName("Should save asset with lastSeen set")
        void saveWithLastSeen() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
            LocalDateTime existingTime = LocalDateTime.now().minusHours(1);
            asset.setLastSeen(existingTime);
            
            when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));

            StepVerifier.create(assetService.save(asset))
                .expectNext(asset)
                .verifyComplete();

            verify(assetRepository).save(asset);
            assertThat(asset.getLastSeen()).isEqualTo(existingTime);
        }

        @Test
        @DisplayName("Should save asset with null lastSeen and set it")
        void saveWithNullLastSeen() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
            asset.setLastSeen(null);
            
            when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));

            StepVerifier.create(assetService.save(asset))
                .expectNext(asset)
                .verifyComplete();

            verify(assetRepository).save(asset);
            assertThat(asset.getLastSeen()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should update asset")
        void update() {
            Asset existingAsset = new Asset("OldName", "OLD_TYPE", "LOW", 0.0, 0.0);
            existingAsset.setId("test-id");
            
            Asset updatedAsset = new Asset("NewName", "NEW_TYPE", "HIGH", 1.0, 2.0);
            
            when(assetRepository.findById("test-id")).thenReturn(Mono.just(existingAsset));
            when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(updatedAsset));

            StepVerifier.create(assetService.update("test-id", updatedAsset))
                .assertNext(asset -> {
                    assertThat(asset.getId()).isEqualTo("test-id");
                })
                .verifyComplete();

            verify(assetRepository).findById("test-id");
            verify(assetRepository).save(any(Asset.class));
        }

        @Test
        @DisplayName("Should return empty when asset not found")
        void updateNotFound() {
            Asset updatedAsset = new Asset("NewName", "NEW_TYPE", "HIGH", 1.0, 2.0);
            
            when(assetRepository.findById("test-id")).thenReturn(Mono.empty());

            StepVerifier.create(assetService.update("test-id", updatedAsset))
                .verifyComplete();

            verify(assetRepository).findById("test-id");
            verify(assetRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update asset status")
        void updateStatus() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
            asset.setId("test-id");
            asset.setStatus("ACTIVE");
            
            when(assetRepository.findById("test-id")).thenReturn(Mono.just(asset));
            when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> 
                Mono.just(invocation.getArgument(0)));

            StepVerifier.create(assetService.updateStatus("test-id", "COMPROMISED"))
                .assertNext(updated -> {
                    assertThat(updated.getStatus()).isEqualTo("COMPROMISED");
                    assertThat(updated.getLastSeen()).isNotNull();
                })
                .verifyComplete();

            verify(assetRepository).findById("test-id");
            verify(assetRepository).save(any(Asset.class));
        }

        @Test
        @DisplayName("Should return empty when updating status for non-existent asset")
        void updateStatusNotFound() {
            when(assetRepository.findById("test-id")).thenReturn(Mono.empty());

            StepVerifier.create(assetService.updateStatus("test-id", "COMPROMISED"))
                .verifyComplete();

            verify(assetRepository).findById("test-id");
            verify(assetRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update asset location")
        void updateLocation() {
            Asset asset = new Asset("Server", "SERVER", "HIGH", 1.0, 2.0);
            asset.setId("test-id");
            
            when(assetRepository.findById("test-id")).thenReturn(Mono.just(asset));
            when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> 
                Mono.just(invocation.getArgument(0)));

            StepVerifier.create(assetService.updateLocation("test-id", 3.0, 4.0))
                .assertNext(updated -> {
                    assertThat(updated.getX()).isEqualTo(3.0);
                    assertThat(updated.getY()).isEqualTo(4.0);
                    assertThat(updated.getLastSeen()).isNotNull();
                })
                .verifyComplete();

            verify(assetRepository).findById("test-id");
            verify(assetRepository).save(any(Asset.class));
        }

        @Test
        @DisplayName("Should return empty when updating location for non-existent asset")
        void updateLocationNotFound() {
            when(assetRepository.findById("test-id")).thenReturn(Mono.empty());

            StepVerifier.create(assetService.updateLocation("test-id", 3.0, 4.0))
                .verifyComplete();

            verify(assetRepository).findById("test-id");
            verify(assetRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("Should delete asset by id")
        void deleteById() {
            when(assetRepository.deleteById("test-id")).thenReturn(Mono.empty());

            StepVerifier.create(assetService.deleteById("test-id"))
                .verifyComplete();

            verify(assetRepository).deleteById("test-id");
        }
    }

    @Nested
    @DisplayName("Count Operations")
    class CountOperations {

        @Test
        @DisplayName("Should count all assets")
        void count() {
            when(assetRepository.count()).thenReturn(Mono.just(10L));

            StepVerifier.create(assetService.count())
                .expectNext(10L)
                .verifyComplete();

            verify(assetRepository).count();
        }

        @Test
        @DisplayName("Should count assets by type")
        void countByType() {
            when(assetRepository.countByType("SERVER")).thenReturn(Mono.just(5L));

            StepVerifier.create(assetService.countByType("SERVER"))
                .expectNext(5L)
                .verifyComplete();

            verify(assetRepository).countByType("SERVER");
        }

        @Test
        @DisplayName("Should count assets by status")
        void countByStatus() {
            when(assetRepository.countByStatus("ACTIVE")).thenReturn(Mono.just(7L));

            StepVerifier.create(assetService.countByStatus("ACTIVE"))
                .expectNext(7L)
                .verifyComplete();

            verify(assetRepository).countByStatus("ACTIVE");
        }
    }
}