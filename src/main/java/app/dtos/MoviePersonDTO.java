package app.dtos;

import app.entities.Movie;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
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
