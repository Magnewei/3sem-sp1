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
@Table(name = "directors")
public class Director {

    @Id
    @Column(name = "director_id", nullable = false, unique = true)
    private int id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "gender",nullable = false)
    private String gender;

    @ManyToMany(mappedBy = "directors")
    private List<Movie> knownFor;


}
