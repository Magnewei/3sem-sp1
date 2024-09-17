package app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Entity
@Data
public class Director {
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

    @ManyToMany(mappedBy = "directors")
    private List<Movie> knownFor;


}
