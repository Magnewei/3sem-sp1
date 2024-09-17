package app.services;

import app.dtos.MovieDTO;
import app.entities.Movie;
import app.entities.MoviePerson;
import app.persistence.daos.MovieDAO;
import app.persistence.daos.MoviePersonDAO;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class MovieService {
    private MoviePersonDAO moviePersonDAO;
    private MovieDAO movieDAO;
    private List<Movie> movies;

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

