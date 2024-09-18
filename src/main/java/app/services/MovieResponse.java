package app.services;

import app.dtos.MovieDTO;
import app.entities.Movie;
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
public class MovieResponse {
    private int page;
    private int total_pages;
    private int total_results;
    private List<MovieDTO> results;
}

