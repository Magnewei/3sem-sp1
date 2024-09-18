package app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Entity
@Data
@Table(name = "actors")
public class Actor {

    @Id
    @Column(name = "actor_id", nullable = false, unique = true)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private int gender;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> knownFor;

    public void addMovie(Movie movie) {
        knownFor.add(movie);
        movie.getActors().add(this);
    }

}
