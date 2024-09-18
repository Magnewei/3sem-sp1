package app.services;

import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.enums.HibernateConfigState;
import app.exceptions.ApiException;
import app.exceptions.JpaException;
import app.persistence.HibernateConfig;
import app.persistence.daos.MovieDAO;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@NoArgsConstructor
public class MovieService {
    private static EntityManagerFactory entityManagerFactory;
    private static MovieDAO movieDAO;
    private static MovieService instance;
    private static ApiService apiService;


    public static synchronized MovieService getInstance(ExecutorService executorService, EntityManagerFactory emf) {
        if (instance == null) {
            instance = new MovieService();
            entityManagerFactory = emf;
            movieDAO = new MovieDAO(entityManagerFactory);
            apiService = ApiService.getInstance(executorService);
        }

        return instance;
    }

    public List<MovieDTO> sortByTitle() {
        List<MovieDTO> allMovies = movieDAO.getAll();
        allMovies.stream()
                .sorted((Comparator.comparing(MovieDTO::getOriginalTitle)))
                .forEach(System.out::println);
        return allMovies;
    }

    public List<MovieDTO> sortByReleaseDate() {
        List<MovieDTO> allmovies = movieDAO.getAll();
        allmovies.stream()
                .sorted(Comparator.comparing(MovieDTO::getReleaseDate))
                .forEach(System.out::println);
        return allmovies;
    }


    public List<MovieDTO> sortByActor(Actor actor) {
        List<MovieDTO> allMovies = movieDAO.getAll();
        allMovies.stream()
                .filter(m -> m.getCast().contains(actor))
                .forEach(System.out::println);
        return allMovies;
    }

    public List<MovieDTO> sortByDirector(Director director) {
        List<MovieDTO> allMovies = movieDAO.getAll();
        allMovies.stream()
                .filter(m -> m.getCast().contains(director))
                .forEach(System.out::println);

        return allMovies;
    }

    public void saveMoviesToDatabase() {
        try {
            List<MovieDTO> movies = apiService.fetchMoviesFromApiEndpoint(1);

             for(MovieDTO movie : movies) {
                movieDAO.create(movie);
            }

        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new JpaException("Could not persist movies to the database.");
        }
    }
}

