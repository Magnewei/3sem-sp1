package app.dtos;

import app.entities.Actor;
import app.entities.Director;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {

    @JsonIgnore
    private int id;

    @JsonProperty("id")
    private int movieId;

    @JsonProperty("overview")
    private String overview;

    private List<ActorDTO> actors = new ArrayList<>();;

    private List<DirectorDTO> directors = new ArrayList<>();;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @JsonProperty("vote_average")
    private double voteAverage;

    @JsonIgnore
    private List<ActorDTO> cast = new ArrayList<>();

    @JsonIgnore

    private List<DirectorDTO> crew = new ArrayList<>();


    public void addActors(List<ActorDTO> actors) {
        cast.addAll(actors);
    }


    public void addDirectors(List<DirectorDTO> directors) {
        crew.addAll(directors);
    }
}
