package app.services;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Director;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;

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
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@NoArgsConstructor
public class ApiService {
    private static ApiService instance;
    private static final String API_KEY = System.getenv("TMDB_API_KEY");
    private static ExecutorService pool;
    private static int movieId = 0;


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
                        movieDTO.setOriginalTitle(movie.getOriginalTitle());
                        movieDTO.setOverview(movie.getOverview());
                        movieDTO.setReleaseDate(movie.getReleaseDate());
                        movieDTO.setVoteAverage(movie.getVoteAverage());
                        movieDTO.setAdult(movie.isAdult());
                        movieDTO.setGenreIds(movie.getGenreIds());

                        try {
                            // Fetch cast and directors
                            HttpRequest castRequest = HttpRequest.newBuilder()
                                    .uri(new URI(String.format(CAST_URL_TEMPLATE, movie.getId())))
                                    .GET()
                                    .build();
                            HttpResponse<String> castResponse = client.send(castRequest, HttpResponse.BodyHandlers.ofString());
                            CreditsResponse creditsResponse = objectMapper.readValue(castResponse.body(), CreditsResponse.class);
                            System.out.println("Raw JSON Response: " + castResponse.body());


                            // Extract actors from the cast
                            if (creditsResponse.getCast() != null) {
                                List<ActorDTO> actors = creditsResponse.getCast().stream()
                                        .map(castMember -> new ActorDTO(
                                                castMember.getId(),
                                                castMember.getName(),
                                                castMember.getGender(),
                                                castMember.getKnownFor()
                                        ))
                                        .collect(Collectors.toList());
                                movieDTO.setCast(actors);
                            }

                            //Get Director
                            JsonNode rootNode = objectMapper.readTree(castResponse.body());

                            // Navigate to the "crew" array in the JSON
                            JsonNode crewArray = rootNode.path("crew");
                            List<Director> directors = new ArrayList<>();
                            for (JsonNode crewNode : crewArray) {
                                // Check if the job is "Director"

                                if ("Director".equalsIgnoreCase(crewNode.path("job").asText())) {
                                    // Deserialize into Director object
                                    Director director = objectMapper.treeToValue(crewNode, Director.class);
                                    directors.add(director);

                                }
                            }
                            movieDTO.setDirectors(directors.stream()
                                    .map(director -> new DirectorDTO(
                                            director.getId(),
                                            director.getName(),
                                            director.getGender(),
                                            director.getKnownFor()
                                    ))
                                    .collect(Collectors.toList()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return movieDTO;
                    }))
                    .collect(Collectors.toList());

            allMovies.addAll(futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            currentPage++;
        }
        return allMovies;
    }



    public List<MovieDTO> fetchActorsFromApiEndPoint(String endpoint) {
        // Implementation to fetch movies from the API
        return new ArrayList<>();
    }


    public List<MovieDTO> fetchDirectorsFromApiEndPoint(String endpoint) {
        // Implementation to fetch movies from the API
        return new ArrayList<>();
    }


}
