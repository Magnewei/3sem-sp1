package app.services;

import app.dtos.MovieDTO;
import app.entities.Movie;
import app.entities.MoviePerson;
import app.persistence.daos.MovieDAO;
import app.persistence.daos.MoviePersonDAO;
import lombok.NoArgsConstructor;

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
    private static ExecutorService executorService;
    private String apiKey = System.getenv("TMDB_API_KEY");;
    private static MoviePersonDAO moviePersonDAO;
    private static MovieDAO movieDAO;
    private static List<Movie> movies;
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
                .sorted((Comparator.comparing(MovieDTO::getTitle)))
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

    public List<MovieDTO> sortByGenre() {
        List<MovieDTO> allmovies = movieDAO.getAll();
        allmovies.stream()
                .sorted((Comparator.comparing(MovieDTO::getGenre)))
                .forEach(System.out::println);
        return allmovies;
    }

    public List<MovieDTO> sortByCast(MoviePerson person) {
        List<MovieDTO> allMovies = movieDAO.getAll();
        allMovies.stream()
                .filter(m -> m.getCast().contains(person))
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

