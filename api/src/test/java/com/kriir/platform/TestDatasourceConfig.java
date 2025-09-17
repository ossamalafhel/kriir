package com.kriir.platform;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

/**
 * Configuration personnalisée pour les tests avec PostgreSQL reactive
 * Résout le problème de DevServices qui ne configure pas correctement les URLs reactive
 */
public class TestDatasourceConfig implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer<?> postgres;

    @Override
    public Map<String, String> start() {
        // Démarre le conteneur PostgreSQL
        postgres = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("quarkus")
                .withUsername("quarkus")
                .withPassword("quarkus");
        
        postgres.start();

        // Configure à la fois les URLs JDBC et reactive
        String jdbcUrl = postgres.getJdbcUrl();
        String reactiveUrl = jdbcUrl.replace("jdbc:postgresql://", "postgresql://");

        return Map.of(
                "quarkus.datasource.jdbc.url", jdbcUrl,
                "quarkus.datasource.reactive.url", reactiveUrl,
                "quarkus.datasource.username", postgres.getUsername(),
                "quarkus.datasource.password", postgres.getPassword(),
                // Disable DevServices when using custom TestContainers
                "quarkus.datasource.devservices.enabled", "false",
                "quarkus.devservices.enabled", "false",
                // Force schema creation
                "quarkus.hibernate-orm.database.generation", "drop-and-create"
        );
    }

    @Override
    public void stop() {
        if (postgres != null) {
            postgres.stop();
        }
    }
}