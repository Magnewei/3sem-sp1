package app.entities;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
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
