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

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "gender",nullable = false)
    private int gender;

    @ManyToMany(mappedBy = "actor")
    private List<Movie> knownFor;

}
