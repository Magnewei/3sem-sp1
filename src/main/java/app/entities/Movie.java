package app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "movie")
public class Movie {
    @Id
    private int id;

    @Column(name = "original_title", nullable = false)
    private String originalTitle;

    @Column(name = "overview", nullable = false)
    private String overview;

    @Column(name = "release_date", nullable = false)
    private String releaseDate;

    @Column(name = "vote_average", nullable = false)
    private double voteAverage;

    @Column(name = "adult", nullable = false)
    private boolean adult;

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> cast;

    @ManyToMany
    @JoinTable(
            name = "movie_director",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    private List<Director> directors;

    @ElementCollection
    private List<Integer> genreIds;
}