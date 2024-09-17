package app.persistence.daos;

import app.dtos.MoviePersonDTO;
import app.entities.MoviePerson;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class MoviePersonDAO implements GenericDAO<MoviePerson> {
    public void save(MoviePersonDTO entity) {
        // Save a movie person
    }

    public void delete(int id) {
        // Delete a movie person
    }

    public boolean update(MoviePersonDTO entity) {
        return false;
        // Update a movie person
    }

    public MoviePerson findById(int id) {
        // Find person by ID
        return new MoviePerson();
    }

    public List<MoviePerson> getAll() {
        // Get all persons
        return new ArrayList<>();
    }

    public MoviePerson toEntity(MoviePersonDTO dto) {
        // Convert DTO to entity
        return new MoviePerson();
    }
}
