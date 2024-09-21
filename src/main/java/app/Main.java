package app;

import app.dtos.MovieDTO;
import app.enums.HibernateConfigState;
import app.persistence.HibernateConfig;
import app.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Main {
    final static EntityManagerFactory entityManagerFactory = HibernateConfig.getEntityManagerFactoryConfig(HibernateConfigState.NORMAL);

    public static void main(String[] args) {
        MovieService movieService = MovieService.getInstance(entityManagerFactory);

        System.out.println("Saving movies to the database...");
        movieService.saveMoviesToDatabase();

        System.out.println("\nMovies sorted by title:");
        List<MovieDTO> moviesSortedByTitle = movieService.sortByTitle();
        moviesSortedByTitle.forEach(System.out::println);
    }
}
