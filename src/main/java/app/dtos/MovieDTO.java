package app.dtos;

import app.entities.Actor;
import app.entities.Director;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

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
    private String originalTitle;
    private String overview;
    private double releaseDate;
    private double voteAverage;
    private boolean adult;
    private List<ActorDTO> cast;
    private List<DirectorDTO> directors;
    private List<Integer> genreIds;
}
