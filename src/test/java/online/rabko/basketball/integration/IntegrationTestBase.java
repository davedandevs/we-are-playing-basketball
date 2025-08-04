package online.rabko.basketball.integration;

import online.rabko.basketball.config.PostgreSqlTestContainer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for integration tests.
 */
@SpringBootTest
public abstract class IntegrationTestBase {

    /**
     * Overrides the database properties with the values from the PostgreSQL test container.
     *
     * @param registry the dynamic property registry
     */
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        PostgreSqlTestContainer container = PostgreSqlTestContainer.getInstance();
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }
}
