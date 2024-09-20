package app.dtos;

import app.entities.Actor;
import app.entities.Director;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {

    @JsonProperty("id")
    private int id;

    @JsonIgnore
    private List<DirectorDTO> directors = new ArrayList<>();

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
}
