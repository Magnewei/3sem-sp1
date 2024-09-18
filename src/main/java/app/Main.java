package app;

import app.enums.HibernateConfigState;
import app.persistence.HibernateConfig;
import app.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    final static ExecutorService pool = Executors.newCachedThreadPool();
    final static EntityManagerFactory entityManagerFactory = HibernateConfig.getEntityManagerFactoryConfig(HibernateConfigState.NORMAL);

    public static void main(String[] args) {
        MovieService movieService = MovieService.getInstance(pool, entityManagerFactory);
        movieService.saveMoviesToDatabase();

    }
}


/*
TODO:
5. Færdiggør MovieService
6. Evt. add nogle Func interfaces?
7. Lav integration testing
8. Add JavaDocs
9. Evt. add nogle kommentarer i metoder?
10. Opdater .PUML diagram
11. Generer ERD diagram

 */