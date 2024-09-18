package app.services;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.exceptions.JpaException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class ApiService {
    private static ApiService instance;
    private static final String API_KEY = System.getenv("TMDB_API_KEY");
    private static ExecutorService pool;

    public static ApiService getInstance(ExecutorService executorService) {
        if (instance == null) {
            instance = new ApiService();
            pool = executorService;
        }
        return instance;
    }

    public List<MovieDTO> fetchMoviesFromApiEndpoint(int numberOfPages) throws URISyntaxException, IOException, InterruptedException {
        String API_URL = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY + "&with_original_language=da&primary_release_date.gte=2019-01-01&sort_by=primary_release_date.desc&page=";
        String CAST_URL_TEMPLATE = "https://api.themoviedb.org/3/movie/%d?api_key=" + API_KEY + "&language=en-US&append_to_response=credits";
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<MovieDTO> allMovies = new ArrayList<>();
        int currentPage = 1;

        while (currentPage <= numberOfPages) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + currentPage))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            MovieResponse movieResponse = objectMapper.readValue(response.body(), MovieResponse.class);

            List<CompletableFuture<MovieDTO>> futures = movieResponse.getResults().stream()
                    .map(movie -> CompletableFuture.supplyAsync(() -> {
                        MovieDTO movieDTO = new MovieDTO();
                        movieDTO.setId(movie.getId());
                        movieDTO.setOriginalTitle(movie.getOriginalTitle());
                        movieDTO.setReleaseDate(movie.getReleaseDate());
                        movieDTO.setVoteAverage(movie.getVoteAverage());
                        movieDTO.setGenreIds(movie.getGenreIds());


                        try {
                            // Fetch cast and directors
                            HttpRequest castRequest = HttpRequest.newBuilder()
                                    .uri(new URI(String.format(CAST_URL_TEMPLATE, movie.getId())))
                                    .GET()
                                    .build();
                            HttpResponse<String> castResponse = client.send(castRequest, HttpResponse.BodyHandlers.ofString());

                            // Parse the response and extract credits
                            JsonNode rootNode = objectMapper.readTree(castResponse.body());
                            JsonNode creditsNode = rootNode.path("credits");

                            // Extract actors from the cast
                            List<ActorDTO> actors = new ArrayList<>();
                            if (creditsNode.has("cast")) {
                                for (JsonNode castNode : creditsNode.get("cast")) {
                                    ActorDTO actor = new ActorDTO();
                                    actor.setId(castNode.get("id").asInt());
                                    actor.setName(castNode.get("name").asText());
                                    actor.setGender(castNode.get("gender").asInt());

                                    List<MovieDTO> knownForMovies = new ArrayList<>();
                                    if (castNode.has("known_for")) {
                                        for (JsonNode knownForNode : castNode.get("known_for")) {
                                            MovieDTO knownMovie = new MovieDTO();
                                            knownMovie.setId(knownForNode.get("id").asInt());
                                            knownMovie.setOriginalTitle(knownForNode.get("title").asText());
                                            knownMovie.setReleaseDate(LocalDate.parse(knownForNode.get("release_date").asText()));
                                            knownForMovies.add(knownMovie);
                                        }
                                    }
                                    actor.setKnownFor(knownForMovies);
                                    actors.add(actor);
                                }
                                movieDTO.setCast(actors);
                            }

                            // Extract director from the crew
                            List<DirectorDTO> directors = new ArrayList<>();
                            if (creditsNode.has("crew")) {
                                for (JsonNode crewNode : creditsNode.get("crew")) {
                                    if ("Director".equalsIgnoreCase(crewNode.get("job").asText())) {
                                        DirectorDTO director = new DirectorDTO();
                                        director.setId(crewNode.get("id").asInt());
                                        director.setName(crewNode.get("name").asText());

                                        // Extract the 'known_for' array and map to MovieDTO
                                        List<MovieDTO> knownForMovies = new ArrayList<>();
                                        if (crewNode.has("known_for")) {
                                            for (JsonNode knownForNode : crewNode.get("known_for")) {
                                                MovieDTO knownMovie = new MovieDTO();
                                                knownMovie.setId(knownForNode.get("id").asInt());
                                                knownMovie.setOriginalTitle(knownForNode.get("title").asText());
                                                knownMovie.setReleaseDate(LocalDate.parse(knownForNode.get("release_date").asText()));
                                                knownForMovies.add(knownMovie);
                                            }
                                        }
                                        director.setKnownFor(knownForMovies);  // Set knownForMovies as MovieDTO list in DirectorDTO

                                        directors.add(director);
                                    }
                                }
                                movieDTO.setDirectors(directors);
                            }

                        } catch (Exception e) {
                            throw new JpaException("Could not fetch data from the TMDB API.");
                        }
                        return movieDTO;
                    }, pool))
                    .toList();

            allMovies.addAll(futures.stream().map(CompletableFuture::join).toList());
            currentPage++;
        }

        return allMovies;
    }

}