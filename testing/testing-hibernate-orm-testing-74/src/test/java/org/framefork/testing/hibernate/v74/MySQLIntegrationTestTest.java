package org.framefork.testing.hibernate.v74;

import jakarta.persistence.EntityManager;
import org.framefork.testing.hibernate.AbstractMySQLIntegrationTest;
import org.framefork.testing.hibernate.database.DatabaseKind;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

final class MySQLIntegrationTestTest extends AbstractMySQLIntegrationTest implements EntityRoundTripContract
{

    @Override
    protected Class<?>[] entities()
    {
        return new Class<?>[]{Book.class};
    }

    @Override
    protected void additionalProperties(final Properties properties)
    {
        properties.setProperty(BATCH_SIZE_SETTING, BATCH_SIZE);
    }

    @Override
    public <T> T returning(final Function<EntityManager, T> work)
    {
        return doInJPA(work);
    }

    @Override
    public void consuming(final Consumer<EntityManager> work)
    {
        doInJPA(work);
    }

    @Override
    public SessionFactory factory()
    {
        return sessionFactory();
    }

    @Override
    public DatabaseKind expectedKind()
    {
        return DatabaseKind.MYSQL;
    }

    @Test
    void doInJPAAcceptsReturningAndVoidBlockLambdas()
    {
        var id = doInJPA(em -> {
            var book = new Book("Neuromancer");
            em.persist(book);
            return book.getId();
        });

        doInJPA(em -> {
            assertThat(em.find(Book.class, id).getTitle()).isEqualTo("Neuromancer");
        });
    }

}
