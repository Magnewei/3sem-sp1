package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "directors")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
