package org.framefork.testing.hibernate;

import org.framefork.testing.hibernate.database.PostgreSQLTestDatabase;
import org.framefork.testing.hibernate.database.TestDatabase;

/**
 * {@link AbstractHibernateIntegrationTest} on the shared {@link PostgreSQLTestDatabase}.
 */
public abstract class AbstractPostgreSQLIntegrationTest extends AbstractHibernateIntegrationTest
{

    @Override
    protected TestDatabase database()
    {
        return PostgreSQLTestDatabase.shared();
    }

}
