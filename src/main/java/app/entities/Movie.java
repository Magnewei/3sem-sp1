package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movies")
public class Movie {

    @Id
    @Column(name = "movie_id", nullable = false, unique = true)
    private int id;

    @Column(name = "original_title", nullable = false)
    private String originalTitle;

    @Column(name = "overview")
    private String overview = "";

    @Column(name = "release_date", nullable = false)
    private String releaseDate;

    @Column(name = "vote_average", nullable = false)
    private double voteAverage;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id"))
    @Column(name = "actors")
    private List<Actor> actors;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "directors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    @Column(name = "directors")
    private List<Director> directors;

    public void addActors(List<Actor> actor) {
        for (Actor a : actor) {
            actors.add(a);
            a.getKnownFor().add(this);
        }
    }


    public void addDirectors(List<Director> director) {
        for (Director d : director) {
            directors.add(d);
            d.getKnownFor().add(this);
        }
    }
}