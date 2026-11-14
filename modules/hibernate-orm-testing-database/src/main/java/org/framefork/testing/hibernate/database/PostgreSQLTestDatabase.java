package org.framefork.testing.hibernate.database;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PostgreSQL in a container, with durability turned off and the data directory on tmpfs, because test data never needs to survive a crash.
 */
public final class PostgreSQLTestDatabase extends ContainerTestDatabase
{

    public static final String DEFAULT_IMAGE = "postgres:16.4";

    private static final Map<String, PostgreSQLTestDatabase> INSTANCES = new ConcurrentHashMap<>();

    private final DockerImageName image;

    private PostgreSQLTestDatabase(final DockerImageName image)
    {
        this.image = image;
    }

    /**
     * Returns the JVM-wide database for {@link #DEFAULT_IMAGE}.
     */
    public static PostgreSQLTestDatabase shared()
    {
        return shared(DEFAULT_IMAGE);
    }

    /**
     * Returns the JVM-wide database for the given Docker image, for example {@code postgres:17.2}.
     */
    public static PostgreSQLTestDatabase shared(final String image)
    {
        return INSTANCES.computeIfAbsent(image, name -> new PostgreSQLTestDatabase(DockerImageName.parse(name).asCompatibleSubstituteFor("postgres")));
    }

    @Override
    public DatabaseKind kind()
    {
        return DatabaseKind.POSTGRESQL;
    }

    @Override
    protected JdbcDatabaseContainer<?> createContainer()
    {
        var container = new PostgreSQLContainer<>(image);
        container.withDatabaseName("framefork");
        container.withUsername("framefork");
        container.withPassword("framefork");
        container.withCommand("postgres", "-c", "fsync=off", "-c", "synchronous_commit=off", "-c", "full_page_writes=off");
        container.withEnv("PGDATA", "/var/lib/postgresql/data");
        container.withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
        return container;
    }

    @Override
    protected DataSource createDataSource(final JdbcDatabaseContainer<?> container)
    {
        var dataSource = new PGSimpleDataSource();
        dataSource.setURL(container.getJdbcUrl());
        dataSource.setUser(container.getUsername());
        dataSource.setPassword(container.getPassword());
        return dataSource;
    }

    @Override
    public String toString()
    {
        return "PostgreSQLTestDatabase[" + image + "]";
    }

}
