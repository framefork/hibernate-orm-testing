package org.framefork.testing.hibernate.database;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

final class TestDatabaseTest
{

    static Stream<TestDatabase> databases()
    {
        return Stream.of(PostgreSQLTestDatabase.shared(), MySQLTestDatabase.shared());
    }

    @ParameterizedTest
    @MethodSource("databases")
    void dataSourceConnectsToTheRunningDatabase(final TestDatabase database) throws SQLException
    {
        try (var connection = database.dataSource().getConnection();
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery("SELECT 1")) {
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getInt(1)).isEqualTo(1);
        }
    }

    @ParameterizedTest
    @MethodSource("databases")
    void sharedReturnsOneInstancePerImage(final TestDatabase database)
    {
        var again = switch (database.kind()) {
            case POSTGRESQL -> PostgreSQLTestDatabase.shared();
            case MYSQL -> MySQLTestDatabase.shared();
        };
        assertThat(again).isSameAs(database);
    }

    @ParameterizedTest
    @MethodSource("databases")
    void exposesTheCredentialsOfTheContainer(final TestDatabase database)
    {
        assertThat(database.username()).isEqualTo("framefork");
        assertThat(database.password()).isEqualTo("framefork");
        assertThat(database.jdbcUrl()).contains("/framefork");
    }

}
