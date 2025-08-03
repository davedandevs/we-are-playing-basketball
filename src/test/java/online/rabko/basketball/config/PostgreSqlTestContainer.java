package online.rabko.basketball.config;

import java.util.Objects;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Test container for PostgreSQL.
 */
public class PostgreSqlTestContainer extends PostgreSQLContainer<PostgreSqlTestContainer> {

    private static final String IMAGE_VERSION = "postgres:16";
    private static PostgreSqlTestContainer container;

    private PostgreSqlTestContainer() {
        super(IMAGE_VERSION);
        this.withReuse(true);
    }

    /**
     * Returns the singleton instance of the PostgreSQLTestContainer.
     *
     * @return the singleton instance of the PostgreSQLTestContainer
     */
    public static PostgreSqlTestContainer getInstance() {
        if (Objects.isNull(container)) {
            container = new PostgreSqlTestContainer();
            container.start();
            System.setProperty("DB_URL", container.getJdbcUrl());
            System.setProperty("DB_USERNAME", container.getUsername());
            System.setProperty("DB_PASSWORD", container.getPassword());
        }
        return container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        super.stop();
    }
}

