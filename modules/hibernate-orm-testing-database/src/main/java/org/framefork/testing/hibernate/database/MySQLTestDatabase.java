package org.framefork.testing.hibernate.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MySQL in a container, with the data directory on tmpfs, because test data never needs to survive a restart.
 */
public final class MySQLTestDatabase extends ContainerTestDatabase
{

    public static final String DEFAULT_IMAGE = "mysql:8.4";

    private static final Map<String, MySQLTestDatabase> INSTANCES = new ConcurrentHashMap<>();

    private final DockerImageName image;

    private MySQLTestDatabase(final DockerImageName image)
    {
        this.image = image;
    }

    /**
     * Returns the JVM-wide database for {@link #DEFAULT_IMAGE}.
     */
    public static MySQLTestDatabase shared()
    {
        return shared(DEFAULT_IMAGE);
    }

    /**
     * Returns the JVM-wide database for the given Docker image, for example {@code mysql:9.1}.
     */
    public static MySQLTestDatabase shared(final String image)
    {
        return INSTANCES.computeIfAbsent(image, name -> new MySQLTestDatabase(DockerImageName.parse(name).asCompatibleSubstituteFor("mysql")));
    }

    @Override
    public DatabaseKind kind()
    {
        return DatabaseKind.MYSQL;
    }

    @Override
    protected JdbcDatabaseContainer<?> createContainer()
    {
        var container = new MySQLContainer<>(image);
        container.withDatabaseName("framefork");
        container.withUsername("framefork");
        container.withPassword("framefork");
        container.withTmpFs(Map.of("/var/lib/mysql", "rw"));
        return container;
    }

    @Override
    protected DataSource createDataSource(final JdbcDatabaseContainer<?> container)
    {
        var dataSource = new MysqlDataSource();
        dataSource.setURL(container.getJdbcUrl());
        dataSource.setUser(container.getUsername());
        dataSource.setPassword(container.getPassword());
        return dataSource;
    }

    @Override
    public String toString()
    {
        return "MySQLTestDatabase[" + image + "]";
    }

}
