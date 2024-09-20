package app.services;

import app.dtos.MovieDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieResponse {
    private int page;
    private int total_pages;
    private int total_results;
    private List<MovieDTO> results;
}

