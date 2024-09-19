package app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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
    @Column(name = "id")
    private int id;

    @Column(name = "director_id", unique = true, nullable = false)
    private int directorId;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private int gender;

}
