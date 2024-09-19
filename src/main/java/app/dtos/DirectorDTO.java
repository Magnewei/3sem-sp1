package app.dtos;

import app.entities.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class DirectorDTO {

    @JsonIgnore
    private int id;

    @JsonIgnore
    private int directorId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private int gender;

    @JsonIgnore
    private List<MovieDTO> knownFor = new ArrayList<>();

}
