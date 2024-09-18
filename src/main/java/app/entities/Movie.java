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
public class Movie {

    @Id
    @Column(name = "movie_id", nullable = false, unique = true)
    private int id;

    @Column(name = "original_title", nullable = false)
    private String originalTitle;

    @Column(name = "overview")
    private String overview;

    @Column(name = "release_date", nullable = false)
    private String releaseDate;

    @Column(name = "vote_average", nullable = false)
    private double voteAverage;

    @ManyToMany
    /*
    @JoinTable(
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )

     */
    private List<Actor> actor;

    @ManyToMany
    /*
    @JoinTable(
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )

     */
    private List<Director> directors;

    @ElementCollection
    private List<Integer> genreIds;
}