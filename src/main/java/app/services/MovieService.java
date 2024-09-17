package app.services;

import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
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
    private static EntityManagerFactory emf;
    private static MovieDAO movieDAO;
    private static MovieService instance;
    private static ApiService apiService;

    public static synchronized MovieService getInstance(ExecutorService executorService, EntityManagerFactory entityManagerFactory) {
        if (instance == null) {
            instance = new MovieService();
            emf = entityManagerFactory;
            movieDAO = new MovieDAO(emf);
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
    /*
    public List<MovieDTO> sortByGenre() {
        List<MovieDTO> allmovies = movieDAO.getAll();
        allmovies.stream()
                .sorted((Comparator.comparing(MovieDTO::getGenre)))
                .forEach(System.out::println);
        return allmovies;
    }
    */


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


    public void fetchDataFromApi() {
        List<MovieDTO> movies;
        try {
            movies = apiService.fetchMoviesFromApiEndpoint(1);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(movies);
    }

    public void saveMoviesToDatabase(List<MovieDTO> movies) {
        // Save movies to the database
    }
}

