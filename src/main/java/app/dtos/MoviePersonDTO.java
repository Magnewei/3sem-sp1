
package app.dtos;

import app.entities.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoviePersonDTO {
    private int id;
    private boolean adult;
    private String name;
    private String gender;
    private String knownForDepartment;
    private String originalName;
    private double popularity;
    private String profilePath;
    private List<Movie> knownFor;
}
