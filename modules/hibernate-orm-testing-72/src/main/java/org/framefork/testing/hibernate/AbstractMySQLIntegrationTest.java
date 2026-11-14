package org.framefork.testing.hibernate;

import org.framefork.testing.hibernate.database.MySQLTestDatabase;
import org.framefork.testing.hibernate.database.TestDatabase;

/**
 * {@link AbstractHibernateIntegrationTest} on the shared {@link MySQLTestDatabase}.
 */
public abstract class AbstractMySQLIntegrationTest extends AbstractHibernateIntegrationTest
{

    @Override
    protected TestDatabase database()
    {
        return MySQLTestDatabase.shared();
    }

}
