package app;

import app.enums.HibernateConfigState;
import app.persistence.HibernateConfig;
import app.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        EntityManagerFactory entityManagerFactory = HibernateConfig.getEntityManagerFactoryConfig(HibernateConfigState.TEST);
        MovieService.getInstance(pool, entityManagerFactory);

    }
}