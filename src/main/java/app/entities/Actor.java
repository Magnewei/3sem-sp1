package app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "actors")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "actor_id", nullable = false)
    private int actorId;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private int gender;

    @ManyToMany(mappedBy = "cast")
    @Column(name = "knownFor")
    private List<Movie> knownFor = new ArrayList<>();

}
