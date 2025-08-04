package online.rabko.basketball.config;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Singleton Testcontainer for PostgreSQL, with reuse support.
 */
public class PostgreSqlTestContainer extends PostgreSQLContainer<PostgreSqlTestContainer> {

    private static final String IMAGE_VERSION = "postgres:16";
    private static final PostgreSqlTestContainer container;

    static {
        container = new PostgreSqlTestContainer();
        container.withReuse(true);
        container.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    private PostgreSqlTestContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgreSqlTestContainer getInstance() {
        return container;
    }
}
