package org.framefork.testing.hibernate.database;

import org.jspecify.annotations.Nullable;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;

/**
 * A {@link TestDatabase} running in a Testcontainers container.
 * <p>
 * The container starts on first use and is never stopped explicitly; Testcontainers removes it when the JVM exits.
 * It is started with {@code withReuse(true)}, so with {@code testcontainers.reuse.enable=true} in {@code ~/.testcontainers.properties}
 * a local developer keeps one container across test runs.
 */
public abstract class ContainerTestDatabase implements TestDatabase
{

    private final Object lock = new Object();

    private volatile @Nullable JdbcDatabaseContainer<?> container;

    /**
     * Returns the running container, starting it first if needed.
     */
    public final JdbcDatabaseContainer<?> container()
    {
        var current = container;
        if (current != null) {
            return current;
        }

        synchronized (lock) {
            current = container;
            if (current == null) {
                current = createContainer();
                current.withReuse(true);
                current.start();
                container = current;
            }
            return current;
        }
    }

    /**
     * Creates the container, configured but not started.
     */
    protected abstract JdbcDatabaseContainer<?> createContainer();

    /**
     * Creates a non-pooling {@link DataSource} for the running container.
     */
    protected abstract DataSource createDataSource(JdbcDatabaseContainer<?> container);

    @Override
    public final String jdbcUrl()
    {
        return container().getJdbcUrl();
    }

    @Override
    public final String username()
    {
        return container().getUsername();
    }

    @Override
    public final String password()
    {
        return container().getPassword();
    }

    @Override
    public final DataSource dataSource()
    {
        return createDataSource(container());
    }

}
