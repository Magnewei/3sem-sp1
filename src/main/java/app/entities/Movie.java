package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private int id;
    private String title;
    private String originalTitle;
    private String overview;
    private String originalLanguage;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private double popularity;
    private double voteAverage;
    private boolean adult;
    private boolean video;
    private List<MoviePerson> cast;
    private List<Integer> genreIds;
}