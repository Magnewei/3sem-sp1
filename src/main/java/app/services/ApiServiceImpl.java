package app.services;

import app.dtos.MovieDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class ApiServiceImpl implements ApiService {
    private static final String API_KEY = "your_api_key";

    @Override
    public List<MovieDTO> fetchMoviesFromApiEndpoint(String endpoint) {
        // Implementation to fetch movies from the API
        return new ArrayList<>();
    }
}
