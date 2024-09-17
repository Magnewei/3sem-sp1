package app.dtos;

import app.entities.MoviePerson;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class MovieDTO {
    private int id;
    private String title;
    private String originalTitle;
    private String overview;
    private String originalLanguage;
    private String backdropPath;
    private String posterPath;
    private double releaseDate;
    private double popularity;
    private double voteCount;
    private double voteAverage;
    private boolean adult;
    private boolean video;
    private List<MoviePerson> cast;
    private List<Integer> genreIds;
}
