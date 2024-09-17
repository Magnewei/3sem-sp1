package app.persistence;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.enums.HibernateConfigState;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HibernateConfig {

    private static EntityManagerFactory entityManagerFactory;

    private static void getAnnotationConfiguration(Configuration configuration) {
        configuration.addAnnotatedClass(Movie.class);
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Director.class);
    }

    private static EntityManagerFactory getEntityManagerFactory(Configuration configuration, Properties props) {
        configuration.setProperties(props);
        getAnnotationConfiguration(configuration);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        System.out.println("Hibernate Java Config serviceRegistry created");

        SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
        return sf.unwrap(EntityManagerFactory.class);
    }

    public static EntityManagerFactory getEntityManagerFactoryConfig(HibernateConfigState state) {
        if (entityManagerFactory == null) {
            if (state == HibernateConfigState.TEST) {
                entityManagerFactory = setupHibernateConfigTest();
            } else {
                entityManagerFactory = buildEntityFactoryConfig();
            }
        }

        return entityManagerFactory;
    }

    private static EntityManagerFactory buildEntityFactoryConfig() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();

            props.put("hibernate.connection.url", System.getenv("DEPLOYED_DB_URL"));
            props.put("hibernate.connection.username", System.getenv("DEPLOYED_DB_USERNAME"));
            props.put("hibernate.connection.password", System.getenv("DEPLOYED_DB_PASSWORD"));

            // props.put("hibernate.show_sql", "true"); // show sql in console
            // props.put("hibernate.format_sql", "true"); // format sql in console
            // props.put("hibernate.use_sql_comments", "true"); // show sql comments in console

            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // dialect for postgresql
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver"); // driver class for postgresql
            props.put("hibernate.archive.autodetection", "class"); // hibernate scans for annotated classes
            props.put("hibernate.current_session_context_class", "thread"); // hibernate current session context
            props.put("hibernate.hbm2ddl.auto", "create"); // hibernate creates tables based on entities
            return getEntityManagerFactory(configuration, props);

        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    private static EntityManagerFactory setupHibernateConfigTest() {
        Configuration configuration = new Configuration();
        Properties props = new Properties();

        if (System.getenv("TEST_DB_URL") != null) {
            props.put("hibernate.connection.url", System.getenv("TEST_DB_URL") + System.getenv("TEST_DB_NAME"));
            props.put("hibernate.connection.username", System.getenv("TEST_DB_USERNAME"));
            props.put("hibernate.connection.password", System.getenv("TEST_DB_PASSWORD"));
            return getEntityManagerFactory(configuration, props);

        } else {
            try {
                props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
                props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test-db");
                props.put("hibernate.connection.username", "postgres");
                props.put("hibernate.connection.password", "postgres");
                props.put("hibernate.archive.autodetection", "class");
                props.put("hibernate.show_sql", "true");
                props.put("hibernate.hbm2ddl.auto", "create-drop");
                return getEntityManagerFactory(configuration, props);

            } catch (Throwable ex) {
                System.err.println("Initial SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
    }
}