package com.lwms.backend.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Safety guard to prevent accidental test execution against production or dev DB.
 *
 * Why?
 * - `spring.jpa.hibernate.ddl-auto=create-drop` in tests will DROP and recreate schema.
 * - If misconfigured to point to MySQL/Postgres (instead of H2), it will erase real data.
 *
 * This guard checks that the datasource URL contains "h2:mem".
 * If not, it throws an exception and aborts the test context startup.
 */
@Configuration
@Profile("test")
public class TestDatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseConfig.class);

    private final Environment environment;

    public TestDatabaseConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void guardAgainstProdDb() {
        // Double-check at runtime that 'test' profile is active to avoid false positives
        boolean isTestProfileActive = Arrays.asList(environment.getActiveProfiles()).contains("test");
        if (!isTestProfileActive) {
            // If for any reason this bean is instantiated without the test profile, do nothing
            logger.info("TestDatabaseConfig loaded but 'test' profile not active; skipping DB guard.");
            return;
        }

        String url = environment.getProperty("spring.datasource.url", "");
        logger.info("Validating test database URL: {}", url);

        if (!url.contains("h2:mem")) {
            throw new IllegalStateException("❌ Test is trying to run on NON-H2 DB! Aborting to protect schema/data.");
        }

        logger.info("✅ Safe: Test is running against H2 in-memory DB.");
    }
}
