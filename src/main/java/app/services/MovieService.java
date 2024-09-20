package app.services;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.exceptions.JpaException;
import app.persistence.daos.MovieDAO;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Service class for managing movies, including fetching, sorting, and saving movies to the database.
 * This service interacts with the API and the database layer to provide movie data.
 *
 */
@NoArgsConstructor
public class MovieService {
    private static MovieDAO movieDAO;
    private static MovieService instance;
    private static ApiService apiService;

    /**
     * Gets the singleton instance of the MovieService, initializing the DAO and API service if not already done.
     *
     * @param emf             the EntityManagerFactory used by MovieDAO
     * @return the singleton instance of MovieService
     */
    public static synchronized MovieService getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new MovieService();
            movieDAO = new MovieDAO(emf);
            apiService = ApiService.getInstance();
        }
        return instance;
    }

    /**
     * Sorts and prints all movies by their original title in ascending order.
     *
     * @return a list of MovieDTOs sorted by their original title
     */
    public List<MovieDTO> sortByTitle() {
        List<MovieDTO> allMovies = movieDAO.getAll();
        allMovies.stream()
                .sorted(Comparator.comparing(MovieDTO::getOriginalTitle))
                .forEach(System.out::println);
        return allMovies;
    }

    /**
     * Sorts and prints all movies by their release date in ascending order.
     *
     * @return a list of MovieDTOs sorted by their release date
     */
    public List<MovieDTO> sortByReleaseDate() {
        List<MovieDTO> allMovies = movieDAO.getAll();
        allMovies.stream()
                .sorted(Comparator.comparing(MovieDTO::getReleaseDate))
                .forEach(System.out::println);
        return allMovies;
    }

    /**
     * Filters and prints movies that include a specific actor in their cast.
     *
     * @param actor the actor to filter movies by
     * @return a list of MovieDTOs that include the specified actor
     */
    public List<MovieDTO> sortByActor(Actor actor) {
        List<MovieDTO> allMovies = movieDAO.getAll();
        allMovies.stream()
                .filter(m -> m.getCast().contains(actor))
                .forEach(System.out::println);
        return allMovies;
    }

    /**
     * Filters and prints movies that include a specific director in their cast.
     *
     * @param director the director to filter movies by
     * @return a list of MovieDTOs that include the specified director
     */
    public List<MovieDTO> sortByDirector(Director director) {
        List<MovieDTO> allMovies = movieDAO.getAll();
        allMovies.stream()
                .filter(m -> m.getCast().contains(director))
                .forEach(System.out::println);
        return allMovies;
    }

    /**
     * Fetches movies from the API, filters out duplicate actors and directors, and saves the movies to the database.
     *
     * This method uses sets to ensure that only unique actors and directors are added to each movie.
     *
     * @throws JpaException if there is an error persisting movies to the database
     */
    public void saveMoviesToDatabase() {
        try {
            List<MovieDTO> movies = apiService.fetchMoviesFromApiEndpoint(48);

            // Use sets to track unique actors and directors
            Set<ActorDTO> uniqueActors = new HashSet<>();
            Set<DirectorDTO> uniqueDirectors = new HashSet<>();

            movies.parallelStream().forEach(movie -> {

                // Filter out duplicate actors
                List<ActorDTO> filteredActors = movie.getCast().stream()
                        .filter(actor -> uniqueActors.add(actor))
                        .collect(Collectors.toList());

                // Filter out duplicate directors
                List<DirectorDTO> filteredDirectors = movie.getDirectors().stream()
                        .filter(director -> uniqueDirectors.add(director))
                        .collect(Collectors.toList());

                movie.setCast(filteredActors);
                movie.setDirectors(filteredDirectors);

                movieDAO.create(movie);
            });
        } catch (URISyntaxException | InterruptedException | IOException e) {
            System.err.println(e.getMessage());
            throw new JpaException("Could not persist movies to the database.");
        }
    }
}
