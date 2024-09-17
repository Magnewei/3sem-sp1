package app.services;

import app.dtos.MovieDTO;
import app.entities.Movie;
import app.entities.MoviePerson;
import app.persistence.daos.MovieDAO;
import app.persistence.daos.MoviePersonDAO;
import lombok.NoArgsConstructor;

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


    public void getAllMovies() {
        // Fetch all movies
    }

    public void sortByTitle() {
        // Sort movies by title
    }

    public void sortByReleaseDate() {
        // Sort movies by release date
    }

    public void sortByGenre() {
        // Sort movies by genre
    }

    public void sortByCast(MoviePerson person) {
        // Sort movies by cast
    }

    public void fetchDataFromApi(String endpoint) {
        // Fetch data from API and save to database
    }

    public void saveMoviesToDatabase(List<MovieDTO> movies) {
        // Save movies to the database
    }
}

