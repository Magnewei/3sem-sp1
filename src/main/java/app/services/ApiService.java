package app.services;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.exceptions.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApiService {
    private static ApiService instance;
    private static final String API_KEY = System.getenv("TMDB_API_KEY");
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    /**
     * Fetches a list of movies from the API, processing multiple pages.
     *
     * @param numberOfPages The number of pages to fetch
     * @return List of MovieDTO objects
     */
    public List<MovieDTO> fetchMoviesFromApiEndpoint(int numberOfPages) throws URISyntaxException, IOException, InterruptedException {
        List<MovieDTO> allMovies = new ArrayList<>();
        int currentPage = 1;

        while (currentPage <= numberOfPages) {
            List<MovieDTO> moviesFromPage = fetchMoviesFromPage(currentPage);
            allMovies.addAll(moviesFromPage);
            currentPage++;
        }

        return allMovies;
    }

    /**
     * Fetches movies from a specific page using the provided ExecutorService.
     *
     * @param page The page number to fetch movies from
     * @return List of MovieDTO objects from the page
     */
    private List<MovieDTO> fetchMoviesFromPage(int page) throws URISyntaxException, IOException, InterruptedException {
        String API_URL = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY + "&with_original_language=da&primary_release_date.gte=2019-01-01&sort_by=primary_release_date.desc&page=";
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<MovieDTO> movies = new ArrayList<>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL + page))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Failed to fetch movies: " + response.body());
            return movies;
        }

        MovieResponse movieResponse = objectMapper.readValue(response.body(), MovieResponse.class);
        List<Future<MovieDTO>> futures = new ArrayList<>();

        for (MovieDTO movie : movieResponse.getResults()) {
            futures.add(executorService.submit(() -> createMovieDTO(movie, client, objectMapper)));
        }

        // Collect results from the Future objects
        for (Future<MovieDTO> future : futures) {
            try {
                movies.add(future.get());

            } catch (ExecutionException | InterruptedException e) {
                System.err.println("Error processing movie: " + e.getMessage());
            }
        }

        return movies;
    }

    /**
     * Creates a MovieDTO object and fetches additional cast and director information.
     *
     * @param movie The Movie entity to process
     * @param client The HttpClient used for making API requests
     * @param objectMapper The ObjectMapper for parsing JSON responses
     * @return The populated MovieDTO object
     */
    private MovieDTO createMovieDTO(MovieDTO movie, HttpClient client, ObjectMapper objectMapper) {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(movie.getId());
        movieDTO.setGenres(movie.getGenres());
        movieDTO.setOriginalTitle(movie.getOriginalTitle());
        movieDTO.setReleaseDate(movie.getReleaseDate());
        movieDTO.setVoteAverage(movie.getVoteAverage());

        fetchCastAndDirectorInfo(movie, movieDTO, client, objectMapper);
        return movieDTO;
    }

    /**
     * Fetches cast and director information for the given movie and updates the MovieDTO.
     *
     * @param movie The Movie entity being processed
     * @param movieDTO The MovieDTO to update with cast and director information
     * @param client The HttpClient used for making API requests
     * @param objectMapper The ObjectMapper for parsing JSON responses
     */
    private void fetchCastAndDirectorInfo(MovieDTO movie, MovieDTO movieDTO, HttpClient client, ObjectMapper objectMapper) {
        String CAST_URL_TEMPLATE = "https://api.themoviedb.org/3/movie/%d?api_key=" + API_KEY + "&language=en-US&append_to_response=credits";

        try {
            String castUrl = String.format(CAST_URL_TEMPLATE, movie.getId());
            HttpRequest castRequest = HttpRequest.newBuilder()
                    .uri(new URI(castUrl))
                    .GET()
                    .build();

            HttpResponse<String> castResponse = client.send(castRequest, HttpResponse.BodyHandlers.ofString());

            if (castResponse.statusCode() != 200) {
                System.err.println("Failed to fetch cast information: " + castResponse.body());
                return;
            }

            JsonNode rootNode = objectMapper.readTree(castResponse.body());
            JsonNode creditsNode = rootNode.path("credits");

            if (creditsNode.isMissingNode()) {
                System.err.println("Credits not found for movie ID: " + movie.getId());
                return;
            }

            List<ActorDTO> actors = extractActors(creditsNode);
            List<DirectorDTO> directors = extractDirectors(creditsNode);
            movieDTO.setCast(actors);
            movieDTO.setDirectors(directors);

        } catch (Exception e) {
            throw new ApiException("Error fetching cast for movie ID: " + movie.getId());
        }
    }

    /**
     * Extracts a list of actors from the credits JSON node.
     *
     * @param creditsNode The JSON node containing credits information
     * @return A list of ActorDTO objects
     */
    private List<ActorDTO> extractActors(JsonNode creditsNode) {
        List<ActorDTO> actors = new ArrayList<>();
        JsonNode castNode = creditsNode.get("cast");
        if (castNode != null && castNode.isArray()) {
            for (JsonNode node : castNode) {
                ActorDTO actor = new ActorDTO();
                actor.setActorId(node.get("id").asInt());
                actor.setName(node.get("name").asText());
                actor.setGender(node.get("gender").asInt());
                actors.add(actor);
            }
        }
        return actors;
    }

    /**
     * Extracts a list of directors from the credits JSON node.
     *
     * @param creditsNode The JSON node containing credits information
     * @return A list of DirectorDTO objects
     */
    private List<DirectorDTO> extractDirectors(JsonNode creditsNode) {
        List<DirectorDTO> directors = new ArrayList<>();
        JsonNode crewNode = creditsNode.get("crew");
        if (crewNode != null && crewNode.isArray()) {
            for (JsonNode node : crewNode) {
                if ("Director".equals(node.get("job").asText())) {
                    DirectorDTO director = new DirectorDTO();
                    director.setName(node.get("name").asText());
                    director.setGender(node.get("gender").asInt());
                    directors.add(director);
                }
            }
        }
        return directors;
    }
}
