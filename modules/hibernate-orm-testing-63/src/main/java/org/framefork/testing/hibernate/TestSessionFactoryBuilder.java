package org.framefork.testing.hibernate;

import org.framefork.testing.hibernate.database.TestDatabase;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builds a {@link SessionFactory} through Hibernate's native bootstrap, connected to a {@link TestDatabase}.
 * <p>
 * The schema is created from the annotated classes when the factory is built, and dropped when it is closed ({@code hibernate.hbm2ddl.auto=create-drop}).
 * Hibernate picks the dialect from the JDBC connection metadata. Every default can be overridden with {@link #setting(String, Object)}.
 */
public final class TestSessionFactoryBuilder
{

    public static final String DATASOURCE_SETTING = "hibernate.connection.datasource";
    public static final String SCHEMA_ACTION_SETTING = "hibernate.hbm2ddl.auto";

    private final TestDatabase database;
    private final List<Class<?>> annotatedClasses = new ArrayList<>();
    private final Map<String, Object> settings = new LinkedHashMap<>();

    private TestSessionFactoryBuilder(final TestDatabase database)
    {
        this.database = database;
        this.settings.put(SCHEMA_ACTION_SETTING, "create-drop");
    }

    public static TestSessionFactoryBuilder forDatabase(final TestDatabase database)
    {
        return new TestSessionFactoryBuilder(Objects.requireNonNull(database, "database must not be null"));
    }

    public TestSessionFactoryBuilder annotatedClasses(final Class<?>... classes)
    {
        annotatedClasses.addAll(Arrays.asList(classes));
        return this;
    }

    public TestSessionFactoryBuilder setting(final String name, final Object value)
    {
        settings.put(name, value);
        return this;
    }

    public TestSessionFactoryBuilder settings(final Map<String, ?> values)
    {
        settings.putAll(values);
        return this;
    }

    public SessionFactory build()
    {
        var effectiveSettings = new LinkedHashMap<String, Object>();
        effectiveSettings.put(DATASOURCE_SETTING, database.dataSource());
        effectiveSettings.putAll(settings);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySettings(effectiveSettings)
            .build();

        try {
            var sources = new MetadataSources(registry);
            annotatedClasses.forEach(sources::addAnnotatedClass);
            return sources.buildMetadata().buildSessionFactory();

        } catch (RuntimeException e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }

}
