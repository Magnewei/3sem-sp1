package app.dtos;

import app.entities.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class DirectorDTO {
    private int id;
    private String name;
    private String gender;
    private List<MovieDTO> knownFor;


}
