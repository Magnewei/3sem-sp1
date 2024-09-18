package app.persistence.daos;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
            if (movieDTO.getActors() != null)
                movie.addActors(movieDTO.getActors().stream().map(this::toActor).collect(Collectors.toList()));


            if (movieDTO.getDirectors() != null)
                movie.addDirectors((movieDTO.getDirectors().stream().map(this::toDirector).collect(Collectors.toList())));


            em.getTransaction().begin();
            em.persist(movie);
            em.getTransaction().commit();

        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            throw new JpaException("Could not create movie.");

        }
    }



    @Transactional
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
        movie.setId(dto.getId());
        movie.setOriginalTitle(dto.getOriginalTitle());
        movie.setReleaseDate(dto.getReleaseDate().toString());
        movie.setOverview(dto.getOverview());
        movie.setVoteAverage(dto.getVoteAverage());
        movie.setActors(dto.getActors().stream().map(this::toActor).collect(Collectors.toList()));
        movie.setDirectors(dto.getDirectors().stream().map(this::toDirector).collect(Collectors.toList()));

        return movie;
    }

    private Actor toActor(ActorDTO dto) {
        if (dto == null) return null;

        Actor actor = new Actor();
        actor.setId(dto.getId());
        actor.setName(dto.getName());
        actor.setGender(dto.getGender());
        actor.setKnownFor(dto.getKnownFor().stream().map(this::toEntity).collect(Collectors.toList()));
        return actor;
    }

    private Director toDirector(DirectorDTO dto) {
        if (dto == null) return null;

        Director director = new Director();
        director.setId(dto.getId());
        director.setName(dto.getName());
        director.setGender(dto.getGender());
        director.setKnownFor(dto.getKnownFor().stream().map(this::toEntity).collect(Collectors.toList()));
        return director;
    }

    @Override
    public MovieDTO toDTO(Movie movie) {
        if (movie == null) return null;

        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setOriginalTitle(movie.getOriginalTitle());
        dto.setReleaseDate(LocalDate.parse(movie.getReleaseDate()));
        dto.setActors(movie.getActors().stream().map(this::toActorDTO).collect(Collectors.toList()));
        dto.setDirectors(movie.getDirectors().stream().map(this::toDirectorDTO).collect(Collectors.toList()));
        dto.setOverview(movie.getOverview());
        dto.setVoteAverage(movie.getVoteAverage());

        return dto;
    }


    public ActorDTO toActorDTO(Actor actor) {
        if (actor == null) return null;

        ActorDTO dto = new ActorDTO();
        dto.setId(actor.getId());
        dto.setName(actor.getName());
        dto.setGender(actor.getGender());
        dto.setKnownFor(actor.getKnownFor().stream().map(this::toDTO).collect(Collectors.toList()));
        return dto;
    }

    public DirectorDTO toDirectorDTO(Director director) {
        if (director == null) return null;

        DirectorDTO dto = new DirectorDTO();
        dto.setId(director.getId());
        dto.setName(director.getName());
        dto.setGender(director.getGender());
        dto.setKnownFor(director.getKnownFor().stream().map(this::toDTO).collect(Collectors.toList()));
        return dto;
    }


    private void prePersistActors(Movie movie, EntityManager em) {
        movie.getActors().forEach(actor -> {
            em.persist(actor);
            em.getTransaction().commit();
        });
    }

    private void prePersistDirectors(Movie movie, EntityManager em) {
        movie.getDirectors().forEach(director -> {
                em.persist(director);
                em.getTransaction().commit();
        });
    }

}


