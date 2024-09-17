package app.dtos;

import app.entities.Movie;
import lombok.Data;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Data
public class ActorDTO {
    private int id;
    private boolean adult;
    private String name;
    private String gender;
    private String knownForDepartment;
    private String originalName;
    private List<Movie> knownFor;
}
