package app.persistence.daos;

import app.dtos.MovieDTO;
import app.entities.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class MovieDAO implements GenericDAO<MovieDTO> {
    public void save(MovieDTO entity) {
        // Save the movie entity
    }

    public void delete(int id) {
        // Delete a movie
    }

    public boolean update(MovieDTO entity) {
        return false;
        // Update a movie
    }

    public Movie findById(int id) {
        // Find movie by ID
        return new Movie();
    }

    public List<Movie> findAll() {
        // Find all movies
        return new ArrayList<>();
    }

    public Movie toEntity(MovieDTO dto) {
        // Convert MovieDTO to Movie entity
        return new Movie();
    }
}

