package org.framefork.testing.hibernate.v64;

import jakarta.persistence.EntityManager;
import org.framefork.testing.hibernate.database.DatabaseKind;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The checks every per-database test class runs; the implementations delegate to the base class under test.
 */
interface EntityRoundTripContract
{

    String BATCH_SIZE_SETTING = "hibernate.jdbc.batch_size";
    String BATCH_SIZE = "7";

    <T> T returning(Function<EntityManager, T> work);

    void consuming(Consumer<EntityManager> work);

    SessionFactory factory();

    DatabaseKind expectedKind();

    @Test
    default void persistedEntityIsReadBackInTheNextTransaction()
    {
        Long id = returning(em -> {
            var book = new Book("Dune");
            em.persist(book);
            return book.getId();
        });

        assertThat(id).isNotNull();
        consuming(em -> {
            var book = em.find(Book.class, id);
            assertThat(book).isNotNull();
            assertThat(book.getTitle()).isEqualTo("Dune");
        });
    }

    @Test
    default void everyTestStartsOnAnEmptySchema()
    {
        assertThat(countBooks()).isZero();
        consuming(em -> em.persist(new Book("Solaris")));
        assertThat(countBooks()).isEqualTo(1L);
    }

    @Test
    default void exceptionRollsBackAndPropagatesUnchanged()
    {
        var failure = new IllegalStateException("expected by the test");

        assertThatThrownBy(() -> consuming(em -> {
            em.persist(new Book("Hyperion"));
            em.flush();
            throw failure;
        })).isSameAs(failure);

        assertThat(countBooks()).isZero();
    }

    @Test
    default void additionalPropertiesReachTheSessionFactory()
    {
        assertThat(factory().getProperties()).containsEntry(BATCH_SIZE_SETTING, BATCH_SIZE);
    }

    @Test
    default void dialectMatchesTheDatabase()
    {
        var dialect = factory().unwrap(SessionFactoryImplementor.class).getJdbcServices().getDialect();
        var expectedName = switch (expectedKind()) {
            case POSTGRESQL -> "PostgreSQL";
            case MYSQL -> "MySQL";
        };
        assertThat(dialect.getClass().getSimpleName()).startsWith(expectedName);
    }

    private long countBooks()
    {
        return returning(em -> em.createQuery("SELECT COUNT(b) FROM Book b", Long.class).getSingleResult());
    }

}
