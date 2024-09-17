package app.services;

import app.dtos.MovieDTO;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@NoArgsConstructor
public class ApiServiceImpl implements ApiService {
    private static final String API_KEY = System.getenv("TMDB_API_KEY");
    private final ExecutorService pool = Executors.newCachedThreadPool();

    @Override
    public List<MovieDTO> fetchMoviesFromApiEndpoint(String endpoint) {
        // Implementation to fetch movies from the API
        return new ArrayList<>();
    }

    @Override
    public List<MovieDTO> fetchActorsFromApiEndPoint(String endpoint) {
        // Implementation to fetch movies from the API
        return new ArrayList<>();
    }

    @Override
    public List<MovieDTO> fetchDirectorsFromApiEndPoint(String endpoint) {
        // Implementation to fetch movies from the API
        return new ArrayList<>();
    }


}
