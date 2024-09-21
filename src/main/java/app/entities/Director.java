package app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "directors")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private int gender;

    @ManyToMany(mappedBy = "directors")
    @Column(name = "knownFor")
    private List<Movie> knownFor = new ArrayList<>();

}
