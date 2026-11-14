package org.framefork.testing.hibernate.database;

/**
 * The database engine behind a {@link TestDatabase}.
 */
public enum DatabaseKind
{

    /**
     * PostgreSQL, see {@link PostgreSQLTestDatabase}.
     */
    POSTGRESQL,

    /**
     * MySQL, see {@link MySQLTestDatabase}.
     */
    MYSQL,

}
