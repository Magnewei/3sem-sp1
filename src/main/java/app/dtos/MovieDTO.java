package app.dtos;

import app.entities.Actor;
import app.entities.Director;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    private int id;
    private String overview;
    private List<ActorDTO> cast;
    private List<DirectorDTO> directors;
    private List<Integer> genreIds;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @JsonProperty("vote_average")
    private double voteAverage;
}
