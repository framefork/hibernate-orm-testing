package org.framefork.testing.hibernate;

import jakarta.persistence.EntityManager;
import org.framefork.testing.hibernate.database.TestDatabase;
import org.hibernate.SessionFactory;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JUnit 5 base for tests that need a {@link SessionFactory} for a few entity classes.
 * <p>
 * Before each test it builds a fresh session factory from {@link #entities()}, which creates the schema; after each test it closes the factory,
 * which drops the schema. Every test therefore starts on empty tables and fresh sequences.
 */
public abstract class AbstractHibernateIntegrationTest
{

    private @Nullable SessionFactory sessionFactory;

    protected abstract TestDatabase database();

    protected abstract Class<?>[] entities();

    /**
     * Adds Hibernate settings on top of the {@link TestSessionFactoryBuilder} defaults.
     */
    protected void additionalProperties(final Properties properties)
    {
    }

    @BeforeEach
    final void buildSessionFactory()
    {
        var properties = new Properties();
        additionalProperties(properties);

        var builder = TestSessionFactoryBuilder.forDatabase(database())
            .annotatedClasses(entities());
        properties.forEach((name, value) -> builder.setting(String.valueOf(name), value));

        sessionFactory = builder.build();
    }

    @AfterEach
    final void closeSessionFactory()
    {
        var current = sessionFactory;
        sessionFactory = null;
        if (current != null) {
            current.close();
        }
    }

    protected final SessionFactory sessionFactory()
    {
        var current = sessionFactory;
        if (current == null) {
            throw new IllegalStateException("The session factory exists only while a test runs");
        }
        return current;
    }

    /**
     * Runs the work in a transaction and returns its result; see {@link TestTransactions}.
     */
    protected final <T extends @Nullable Object> T doInJPA(final Function<EntityManager, T> work)
    {
        return TestTransactions.inTransaction(sessionFactory(), work);
    }

    /**
     * Runs the work in a transaction; see {@link TestTransactions}.
     * <p>
     * Overloaded with the returning variant on purpose: a block lambda with {@code return value;} resolves to that one, a block lambda without a value to this one.
     * An expression lambda whose expression has a value, such as {@code em -> em.merge(entity)}, matches both and does not compile; write it as a block.
     */
    @SuppressWarnings("overloads")
    protected final void doInJPA(final Consumer<EntityManager> work)
    {
        TestTransactions.runInTransaction(sessionFactory(), work);
    }

}
