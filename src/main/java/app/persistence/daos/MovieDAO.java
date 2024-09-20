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
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for managing Movie entities. This class provides
 * methods to perform CRUD operations on movies, including converting between
 * DTOs and entities.
 */
public class MovieDAO implements GenericDAO<MovieDTO, Movie> {
    private final EntityManagerFactory emf;

    /**
     * Constructs a MovieDAO with the given EntityManagerFactory.
     *
     * @param entityManagerFactory the {@link EntityManagerFactory} used for managing entities.
     */
    public MovieDAO(EntityManagerFactory entityManagerFactory) {
        emf = entityManagerFactory;
    }

    /**
     * Creates a new Movie entity in the database from the given MovieDTO.
     *
     * @param movieDTO the {@link MovieDTO} object to be persisted.
     * @throws JpaException if the movie already exists or there is an error creating the movie.
     */
    @Override
    public MovieDTO create(MovieDTO movieDTO) {
        Movie movie = toEntity(movieDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(movie);
            em.getTransaction().commit();

            movieDTO.setId(movie.getId());
        } catch (EntityExistsException e) {
            throw new JpaException("Movie already exists.");
        } catch (Exception e) {
            throw new JpaException("Could not create movie.");
        }
        return movieDTO;
    }

    /**
     * Deletes the given Movie entity from the database.
     *
     * @param movieDTO the {@link MovieDTO} object to be deleted.
     * @throws JpaException if there is an error deleting the movie.
     */
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

    /**
     * Retrieves a Movie entity by its ID and converts it to a MovieDTO.
     *
     * @param id the ID of the movie to retrieve.
     * @return the {@link MovieDTO} object representing the retrieved movie.
     * @throws JpaException if the movie could not be found.
     */
    @Transactional
    @Override
    public MovieDTO getById(int id) {
        try (var em = emf.createEntityManager()) {
            Movie movie = em.createQuery("SELECT m FROM Movie m WHERE m.id = :id", Movie.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return toDTO(movie);

        } catch (NoResultException e) {
            // Return null if no movie is found
            return null;
        } catch (Exception e) {
            throw new JpaException("Could not find movie. " + e.getMessage());
        }
    }

    /**
     * Retrieves all Movie entities from the database and converts them to a list of MovieDTOs.
     *
     * @return a {@link List} of {@link MovieDTO} objects representing all movies in the database.
     * @throws JpaException if there is an error retrieving the movies.
     */
    @Override
    public List<MovieDTO> getAll() {
        try (var em = emf.createEntityManager()) {
            List<Movie> movies = em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
            return movies.stream().map(this::toDTO).toList();

        } catch (Exception e) {
            throw new JpaException("Could not get all movies.");
        }
    }

    /**
     * Updates the given Movie entity in the database.
     *
     * @param movieDTO the {@link MovieDTO} object containing updated movie data.
     * @throws JpaException if there is an error updating the movie.
     */
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

    /**
     * Converts a MovieDTO to a Movie entity.
     *
     * @param dto the {@link MovieDTO} object to convert.
     * @return the converted {@link Movie} entity.
     */
    @Override
    public Movie toEntity(MovieDTO dto) {
        if (dto == null) return null;

        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setOriginalTitle(dto.getOriginalTitle());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setVoteAverage(dto.getVoteAverage());

        // Set cast
        if (dto.getCast() != null) {
            List<Actor> actors = dto.getCast().stream()
                    .map(this::toActor)
                    .collect(Collectors.toList());
            movie.setCast(actors);
        }

        // Set director(s)
        if (dto.getDirectors() != null) {
            List<Director> directors = dto.getDirectors().stream()
                    .map(this::toDirector)
                    .collect(Collectors.toList());
            movie.setDirectors(directors);
        }
        return movie;
    }

    /**
     * Converts an ActorDTO to an Actor entity.
     *
     * @param dto the {@link ActorDTO} object to convert.
     * @return the converted {@link Actor} entity.
     */
    private Actor toActor(ActorDTO dto) {
        if (dto == null) return null;

        Actor actor = new Actor();
        actor.setName(dto.getName());

        if (dto.getGender() != 0) actor.setGender(dto.getGender());

        return actor;
    }

    /**
     * Converts a DirectorDTO to a Director entity.
     *
     * @param dto the {@link DirectorDTO} object to convert.
     * @return the converted {@link Director} entity.
     */
    private Director toDirector(DirectorDTO dto) {
        if (dto == null) return null;

        Director director = new Director();
        director.setName(dto.getName());
        director.setGender(dto.getGender());
        return director;
    }

    /**
     * Converts a Movie entity to a MovieDTO.
     *
     * @param movie the {@link Movie} entity to convert.
     * @return the converted {@link MovieDTO} object.
     */
    @Override
    public MovieDTO toDTO(Movie movie) {
        if (movie == null) return null;

        MovieDTO dto = new MovieDTO();
        if (movie.getId() != 0) {
            dto.setId(movie.getId());
        }

        dto.setOriginalTitle(movie.getOriginalTitle());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setVoteAverage(movie.getVoteAverage());

        return dto;
    }

    /**
     * Converts an Actor entity to an ActorDTO.
     *
     * @param actor the {@link Actor} entity to convert.
     * @return the converted {@link ActorDTO} object.
     */
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

    /**
     * Converts a Director entity to a DirectorDTO.
     *
     * @param director the {@link Director} entity to convert.
     * @return the converted {@link DirectorDTO} object.
     */
    public DirectorDTO toDirectorDTO(Director director) {
        if (director == null) return null;

        DirectorDTO dto = new DirectorDTO();
        dto.setName(director.getName());
        dto.setGender(director.getGender());
        return dto;
    }
}