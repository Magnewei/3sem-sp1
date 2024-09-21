package app.persistence.daos;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.JpaException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
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
     * Creates a new movie record in the database, ensuring that associated genres are managed and persisted first.
     * The method first converts the incoming {@link MovieDTO} into a {@link Movie} entity, persists the genres,
     * and then merges the movie entity with managed genres to prevent transient object exceptions.
     *
     * This approach ensures that all genres associated with the movie are in a managed state before the movie is saved.
     * The method manually manages transactions to control the order and success of the operations.
     *
     * @param movieDTO the {@link MovieDTO} object containing the movie details to be created.
     * @return the created {@link MovieDTO} with the updated movie ID set.
     * @throws JpaException if the movie already exists or if any error occurs during the creation process.
     */
    @Override
    public MovieDTO create(MovieDTO movieDTO) {
        Movie movie = toEntity(movieDTO);

        // Persist genres first to ensure they are managed
        persistGenres(movie.getGenres());

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Attach managed genres to the movie to avoid transient object exceptions
            List<Genre> managedGenres = new ArrayList<>();
            for (Genre genre : movie.getGenres()) {
                Genre managedGenre = em.find(Genre.class, genre.getId());
                if (managedGenre != null) {
                    managedGenres.add(managedGenre);
                }
            }
            movie.setGenres(managedGenres);

            em.merge(movie);
            em.getTransaction().commit();

            movieDTO.setId(movie.getId());
        } catch (EntityExistsException e) {
            throw new JpaException("Movie already exists." + e.getMessage());
        } catch (Exception e) {
            throw new JpaException("Could not create movie." + e.getMessage() + e.getCause());
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
            throw new JpaException("Could not delete movie" + e.getMessage());
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
            throw new JpaException("Could not get all movies." + e.getMessage());
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
            throw new JpaException("Could not update movie." + e.getMessage());
        }
    }

    /**
     * Gets the total average rating of all movies in the database.
     *
     * @return the average rating of all movies.
     * @throws JpaException if there is an error retrieving the average rating.
     */
    public double getTotalAverageRating() {
        try (var em = emf.createEntityManager()) {
            Double averageRating = em.createQuery("SELECT AVG(m.voteAverage) FROM Movie m", Double.class)
                    .getSingleResult();

            return averageRating != null ? averageRating : 0.0;

        } catch (Exception e) {
            throw new JpaException("Could not get total average rating." + e.getMessage());
        }
    }

    /**
     * Gets the top-10 lowest rated movies in the database.
     *
     * @return a list of the top-10 lowest rated movies.
     * @throws JpaException if there is an error retrieving the movies.
     */
    public List<MovieDTO> getTop10LowestRatedMovies() {
        try (var em = emf.createEntityManager()) {
            List<Movie> movies = em.createQuery("SELECT m FROM Movie m ORDER BY m.voteAverage ASC", Movie.class)
                    .setMaxResults(10)
                    .getResultList();

            return movies.stream().map(this::toDTO).collect(Collectors.toList());

        } catch (Exception e) {
            throw new JpaException("Could not get top-10 lowest rated movies." + e.getMessage());
        }
    }

    /**
     * Persists a list of genres into the database. If a genre already exists, it will not be duplicated.
     * This method first checks if the genres are already present in the database by querying by genre name.
     * If not found, it persists the genre and flushes to clear the queue.
     *
     * The transaction is managed manually to ensure that all changes are committed together.
     *
     * @param genres the list of {@link Genre} objects to persist; if the list is null or empty, the method returns immediately.
     * @throws JpaException if any error occurs during the persistence process, encapsulating the error message.
     */
    @Transactional
    public void persistGenres(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            for (Genre genre : genres) {
                List<Genre> existingGenres = em.createQuery("SELECT g FROM Genre g WHERE g.genre = :genreName", Genre.class)
                        .setParameter("genreName", genre.getGenre())
                        .getResultList();

                if (existingGenres.isEmpty()) {
                    // Save the genre if it's not in the database & flush to clear the queue.
                    em.persist(genre);
                    em.flush();
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Could not persist genres. " + e.getMessage());
        }
    }

    /**
     * Gets the top-10 highest rated movies in the database.
     *
     * @return a list of the top-10 highest rated movies.
     * @throws JpaException if there is an error retrieving the movies.
     */
    public List<MovieDTO> getTop10HighestRatedMovies() {
        try (var em = emf.createEntityManager()) {
            List<Movie> movies = em.createQuery("SELECT m FROM Movie m ORDER BY m.voteAverage DESC", Movie.class)
                    .setMaxResults(10)
                    .getResultList();

            return movies.stream().map(this::toDTO).collect(Collectors.toList());

        } catch (Exception e) {
            throw new JpaException("Could not get top-10 highest rated movies." + e.getMessage());
        }
    }

    /**
     * Retrieves all movies with the given title.
     *
     * @param title the title of the movies to retrieve.
     * @return a list of MovieDTOs with the given title.
     * @throws JpaException if there is an error retrieving the movies.
     */
    public List<MovieDTO> getMoviesByTitle(String title) {
        try (var em = emf.createEntityManager()) {
            List<Movie> movies = em.createQuery("SELECT m FROM Movie m WHERE m.originalTitle = :title", Movie.class)
                    .setParameter("title", title)
                    .getResultList();
            return movies.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new JpaException("Could not get movies by title.");
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

        movie.setOriginalTitle(dto.getOriginalTitle());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setVoteAverage(dto.getVoteAverage());

        if (dto.getId() != 0) {
            movie.setId(dto.getId());
        }

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

        // Set genres. Using a normal loop for readability, due to conversion from Integer to an object.
        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>();

            for (Integer genreDTO : dto.getGenres()) {
                Genre genre = new Genre();
                genre.setGenre(getGenreNameById(genreDTO));
                genres.add(genre);
            }

            movie.setGenres(genres);
        }

        return movie;
    }

    private Genre toGenre(GenreDTO dto) {
        if (dto == null) return null;

        // Do not persist the ID of the entity.
        Genre genre = new Genre();
        genre.setGenre(dto.getGenreName());
        return genre;
    }

    private String getGenreNameById(int id) {
        return switch (id) {
            case 28 -> "ACTION";
            case 12 -> "ADVENTURE";
            case 16 -> "ANIMATION";
            case 35 -> "COMEDY";
            case 80 -> "CRIME";
            case 99 -> "DOCUMENTARY";
            case 18 -> "DRAMA";
            case 10751 -> "FAMILY";
            case 14 -> "FANTASY";
            case 36 -> "HISTORY";
            case 27 -> "HORROR";
            case 10402 -> "MUSIC";
            case 9648 -> "MYSTERY";
            case 10749 -> "ROMANCE";
            case 878 -> "SCIENCE_FICTION";
            case 10770 -> "TV_MOVIE";
            case 53 -> "THRILLER";
            case 10752 -> "WAR";
            case 37 -> "WESTERN";
            default -> throw new IllegalArgumentException("No genre found with ID: " + id);
        };
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