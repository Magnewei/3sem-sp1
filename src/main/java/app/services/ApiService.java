package app.services;


import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ApiService {
    private static ApiService instance;
    private static final String API_KEY = System.getenv("TMDB_API_KEY");
    private static int movieId = 0;
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
                    .header("accept", "application/json")
                    .version(HttpClient.Version.HTTP_2)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            MovieResponse movieResponse = objectMapper.readValue(response.body(), MovieResponse.class);

            List<CompletableFuture<MovieDTO>> futures = movieResponse.getResults().stream()
                    .map(movie -> CompletableFuture.supplyAsync(() -> {
                        MovieDTO movieDTO = new MovieDTO();
                        movieDTO.setMovieId(String.valueOf(movie.getMovieId()));
                        movieDTO.setOriginalTitle(movie.getOriginalTitle());
                        movieDTO.setReleaseDate(movie.getReleaseDate());
                        movieDTO.setVoteAverage(movie.getVoteAverage());

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

                            // Add actors and director(s) to the DTO.
                            List<ActorDTO> actors = extractActors(creditsNode, movieDTO);
                            List<DirectorDTO> directors = extractDirectors(creditsNode, movieDTO);
                            movieDTO.addActors(actors);
                            movieDTO.addDirectors(directors);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return movieDTO;
                    }, pool))
                    .toList();

            allMovies.addAll(futures.stream().map(CompletableFuture::join).toList());
            currentPage++;
        }

        return allMovies;
    }


    private List<ActorDTO> extractActors(JsonNode creditsNode, MovieDTO movieDTO) {
        List<ActorDTO> actors = new ArrayList<>();
        JsonNode castNode = creditsNode.get("cast");
        if (castNode != null && castNode.isArray()) {
            for (JsonNode node : castNode) {
                ActorDTO actor = new ActorDTO();
                actor.setActorId(node.get("id").asInt());
                actor.setName(node.get("name").asText());
                actor.setGender(node.get("gender").asInt());
                actor.getKnownFor().add(movieDTO);
                actors.add(actor);
            }
        }

        return actors;
    }

    private List<DirectorDTO> extractDirectors(JsonNode creditsNode, MovieDTO movieDTO) {
        List<DirectorDTO> directors = new ArrayList<>();
        JsonNode crewNode = creditsNode.get("crew");
        if (crewNode != null && crewNode.isArray()) {
            for (JsonNode node : crewNode) {
                if ("Director".equals(node.get("job").asText())) {
                    DirectorDTO director = new DirectorDTO();
                    director.setDirectorId(node.get("id").asInt());
                    director.setName(node.get("name").asText());
                    director.getKnownFor().add(movieDTO);
                    directors.add(director);
                }
            }
        }
        return directors;
    }
}
