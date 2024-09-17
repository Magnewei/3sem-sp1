package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class MoviePerson {
    private boolean adult;
    private int id;
    private String name;
    private String gender;
    private String knownForDepartment;
    private String originalName;
    private double popularity;
    private String profilePath;
    private List<Movie> knownFor;
}