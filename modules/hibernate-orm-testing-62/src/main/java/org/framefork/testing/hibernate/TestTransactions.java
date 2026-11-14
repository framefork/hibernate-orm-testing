package org.framefork.testing.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Runs work in a resource-local transaction on a new {@link EntityManager}.
 * <p>
 * The transaction commits when the work returns. When the work throws, the transaction rolls back and the exception propagates unchanged,
 * so tests can assert on the exact exception type. The entity manager is always closed.
 */
public final class TestTransactions
{

    private TestTransactions()
    {
    }

    public static <T extends @Nullable Object> T inTransaction(final EntityManagerFactory entityManagerFactory, final Function<EntityManager, T> work)
    {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            var transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                T result = work.apply(entityManager);
                transaction.commit();
                return result;

            } catch (RuntimeException | Error e) {
                if (transaction.isActive()) {
                    try {
                        transaction.rollback();
                    } catch (RuntimeException rollbackFailure) {
                        e.addSuppressed(rollbackFailure);
                    }
                }
                throw e;
            }

        } finally {
            entityManager.close();
        }
    }

    public static void runInTransaction(final EntityManagerFactory entityManagerFactory, final Consumer<EntityManager> work)
    {
        inTransaction(entityManagerFactory, (Function<EntityManager, @Nullable Void>) entityManager -> {
            work.accept(entityManager);
            return null;
        });
    }

}
