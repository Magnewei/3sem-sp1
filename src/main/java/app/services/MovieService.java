package app.services;

import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.entities.MoviePerson;
import app.enums.HibernateConfigState;
import app.persistence.HibernateConfig;
import app.persistence.daos.MovieDAO;
import app.persistence.daos.MoviePersonDAO;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@NoArgsConstructor
public class MovieService {
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(HibernateConfigState.TEST);
    private static final MoviePersonDAO moviePersonDAO = new MoviePersonDAO(emf);
    private static final MovieDAO movieDAO = new MovieDAO(emf);
    private static MovieService instance;

    public static MovieService getInstance() {
        if (instance == null) {
            instance = new MovieService();
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

   /* public List<MovieDTO> sortByGenre() {
        List<MovieDTO> allmovies = movieDAO.getAll();
        allmovies.stream()
                .sorted((Comparator.comparing(MovieDTO::getGenre)))
                .forEach(System.out::println);
        return allmovies;
    }*/

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

    public void fetchDataFromApi(String endpoint) {
        // Fetch data from API and save to database
    }

    public void saveMoviesToDatabase(List<MovieDTO> movies) {
        // Save movies to the database
    }
}

