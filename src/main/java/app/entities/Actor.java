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
@Table(name = "actor")
public class Actor {
    @Id
    private int id;

    @Column(name = "adult",nullable = false)
    private boolean adult;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "gender",nullable = false)
    private String gender;

    @Column(name = "known_for_department",nullable = false)
    private String knownForDepartment;

    @Column(name = "original_name",nullable = false)
    private String originalName;

    @ManyToMany(mappedBy = "cast")
    private List<Movie> knownFor;
}
