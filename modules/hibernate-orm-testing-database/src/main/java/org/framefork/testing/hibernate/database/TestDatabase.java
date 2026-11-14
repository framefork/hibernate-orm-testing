package org.framefork.testing.hibernate.database;

import javax.sql.DataSource;

/**
 * A database that integration tests connect to.
 * <p>
 * Implementations start their database lazily, on the first call that needs a connection, and keep it running for the rest of the JVM.
 */
public interface TestDatabase
{

    /**
     * Returns which database engine this is.
     */
    DatabaseKind kind();

    /**
     * Returns the JDBC URL of the running database, starting it first if needed.
     */
    String jdbcUrl();

    /**
     * Returns the user name that {@link #dataSource()} connects as.
     */
    String username();

    /**
     * Returns the password that {@link #dataSource()} connects with.
     */
    String password();

    /**
     * Returns a non-pooling {@link DataSource}: every {@code getConnection()} opens a new physical connection.
     */
    DataSource dataSource();

}
