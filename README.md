# Hibernate ORM testing

Primitives for integration tests against Hibernate ORM 6.1 through 8.0, on PostgreSQL and MySQL running in [Testcontainers](https://java.testcontainers.org/).

## Installation

Pick the module matching the Hibernate ORM line your code runs on. Each module brings `hibernate-orm-testing-database` with it.

| Hibernate ORM      | Tested on                    | Artifact                                                                                                                                           |
|--------------------|------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| 8.0                | 8.0.0.CR1                    | [org.framefork.testing:hibernate-orm-testing-80](https://central.sonatype.com/artifact/org.framefork.testing/hibernate-orm-testing-80)             |
| 7.2, 7.3, 7.4      | 7.2.25, 7.3.13, 7.4.15       | [org.framefork.testing:hibernate-orm-testing-72](https://central.sonatype.com/artifact/org.framefork.testing/hibernate-orm-testing-72)             |
| 7.0, 7.1           | 7.0.10, 7.1.36               | [org.framefork.testing:hibernate-orm-testing-70](https://central.sonatype.com/artifact/org.framefork.testing/hibernate-orm-testing-70)             |
| 6.3, 6.4, 6.5, 6.6 | 6.3.2, 6.4.10, 6.5.3, 6.6.59 | [org.framefork.testing:hibernate-orm-testing-63](https://central.sonatype.com/artifact/org.framefork.testing/hibernate-orm-testing-63)             |
| 6.2                | 6.2.52                       | [org.framefork.testing:hibernate-orm-testing-62](https://central.sonatype.com/artifact/org.framefork.testing/hibernate-orm-testing-62)             |
| 6.1                | 6.1.7                        | [org.framefork.testing:hibernate-orm-testing-61](https://central.sonatype.com/artifact/org.framefork.testing/hibernate-orm-testing-61)             |
| none (plain JDBC)  | -                            | [org.framefork.testing:hibernate-orm-testing-database](https://central.sonatype.com/artifact/org.framefork.testing/hibernate-orm-testing-database) |

The modules do not bring `hibernate-core`: they compile against the first release of their line and use whatever `hibernate-core` version your test classpath has.
Every Hibernate ORM minor a module covers is tested at its newest patch, listed in the table.
`hibernate-orm-testing-80` is built against Hibernate ORM `8.0.0.CR1` until 8.0 is GA.

Minimum supported Java is 17. The tests need a Docker daemon that Testcontainers can reach.

## Usage

Extend `AbstractPostgreSQLIntegrationTest` or `AbstractMySQLIntegrationTest` and list the entities the test needs.
Before each test the base class builds a fresh `SessionFactory`, which creates the schema; after each test it closes the factory, which drops the schema.

```java
import org.framefork.testing.hibernate.AbstractPostgreSQLIntegrationTest;

class BookPersistenceTest extends AbstractPostgreSQLIntegrationTest
{

    @Override
    protected Class<?>[] entities()
    {
        return new Class<?>[]{Book.class};
    }

    @Override
    protected void additionalProperties(Properties properties)
    {
        properties.setProperty("hibernate.jdbc.batch_size", "20");
    }

    @Test
    void bookIsReadBack()
    {
        var id = doInJPA(em -> {
            var book = new Book("Dune");
            em.persist(book);
            return book.getId();
        });

        doInJPA(em -> {
            assertThat(em.find(Book.class, id).getTitle()).isEqualTo("Dune");
        });
    }

}
```

`doInJPA` runs the lambda in a transaction on a new `EntityManager` and commits when it returns. When the lambda throws, the transaction rolls back and the exception propagates unchanged.

The parts the base classes are built from can be used on their own:

- `PostgreSQLTestDatabase.shared()` and `MySQLTestDatabase.shared()` (module `hibernate-orm-testing-database`) start one container per Docker image and JVM on first use and give you its JDBC URL, credentials and a `DataSource`. Pass an image to `shared(String)` to test another database version.
- `TestSessionFactoryBuilder.forDatabase(database).annotatedClasses(...).setting(...).build()` builds a `SessionFactory` through Hibernate's native bootstrap, with `hibernate.hbm2ddl.auto=create-drop`.
- `TestTransactions.inTransaction(entityManagerFactory, em -> ...)` and `TestTransactions.runInTransaction(...)` run work in a transaction, as `doInJPA` does.

The containers are started with `withReuse(true)`. Set `testcontainers.reuse.enable=true` in `~/.testcontainers.properties` to keep them running between local test runs.

## License

Apache License 2.0, see [LICENSE](LICENSE).
