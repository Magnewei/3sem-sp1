package app.persistence.daos;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.exceptions.JpaException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class MovieDAO implements GenericDAO<MovieDTO, Movie> {
    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory entityManagerFactory) {
        emf = entityManagerFactory;
    }

    @Override
    public void create(MovieDTO movieDTO) {
        Movie movie = toEntity(movieDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(movie);
            em.getTransaction().commit();

        } catch (EntityExistsException e) {
            throw new JpaException("Movie already exists.");

        } catch (Exception e) {
            throw new JpaException("Could not create movie.");
        }
    }


@Override
public void delete(MovieDTO movieDTO) {
    Movie movie = toEntity(movieDTO);

    try (var em = emf.createEntityManager()) {
        em.getTransaction().begin();
        em.remove(movie);
        em.getTransaction().commit();

    } catch (Exception e) {
        throw new JpaException("Could not delete movie");
    }
}

@Transactional
@Override
public MovieDTO getById(int id) {
    try (var em = emf.createEntityManager()) {
        Movie movie = em.createQuery("SELECT m FROM Movie m WHERE m.id = :id", Movie.class)
                .setParameter("id", id)
                .getSingleResult();

        return toDTO(movie);

    } catch (Exception e) {
        throw new JpaException("Could not find movie.");
    }
}

@Transactional
@Override
public List<MovieDTO> getAll() {
    try (var em = emf.createEntityManager()) {
        List<Movie> movies = em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
        return movies.stream().map(this::toDTO).toList();

    } catch (Exception e) {
        throw new JpaException("Could not get all movies.");
    }
}

@Override
public void update(MovieDTO movieDTO) {
    Movie movie = toEntity(movieDTO);

    try (var em = emf.createEntityManager()) {
        em.getTransaction().begin();
        em.merge(movie);
        em.getTransaction().commit();

    } catch (Exception e) {
        throw new JpaException("Could not update movie.");
    }
}

@Override
public Movie toEntity(MovieDTO dto) {
    if (dto == null) return null;

    Movie movie = new Movie();
    movie.setOriginalTitle(dto.getOriginalTitle());
    movie.setReleaseDate(dto.getReleaseDate());
    movie.setOverview(dto.getOverview());
    movie.setVoteAverage(dto.getVoteAverage());


    return movie;
}


private Actor toActor(ActorDTO dto) {
    if (dto == null) return null;

    Actor actor = new Actor();
    actor.setName(dto.getName());

    if (dto.getGender() != 0) actor.setGender(dto.getGender());


    return actor;
}

private Director toDirector(DirectorDTO dto) {
    if (dto == null) return null;

    Director director = new Director();
    director.setName(dto.getName());
    director.setGender(dto.getGender());
    return director;
}

@Override
public MovieDTO toDTO(Movie movie) {
    if (movie == null) return null;

    MovieDTO dto = new MovieDTO();
    if (movie.getId() != 0) {
        dto.setId(movie.getId());
    }

    dto.setOriginalTitle(movie.getOriginalTitle());
    dto.setReleaseDate(movie.getReleaseDate());
    dto.setOverview(movie.getOverview());
    dto.setVoteAverage(movie.getVoteAverage());

    return dto;
}


public ActorDTO toActorDTO(Actor actor) {
    if (actor == null) return null;

    ActorDTO dto = new ActorDTO();
    if (dto.getId() != 0) {
        dto.setId(actor.getId());
    }

    dto.setName(actor.getName());
    dto.setGender(actor.getGender());
    return dto;
}

public DirectorDTO toDirectorDTO(Director director) {
    if (director == null) return null;

    DirectorDTO dto = new DirectorDTO();
    dto.setName(director.getName());
    dto.setGender(director.getGender());
    return dto;
}



}


