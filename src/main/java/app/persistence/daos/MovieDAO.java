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
    private final ActorDAO actorDAO;
    private final DirectorDAO directorDAO;

    /**
     * Constructs a MovieDAO with the given EntityManagerFactory.
     *
     * @param entityManagerFactory the {@link EntityManagerFactory} used for managing entities.
     */
    public MovieDAO(EntityManagerFactory emf, ActorDAO actorDAO, DirectorDAO directorDAO) {
        this.emf = emf;
        this.actorDAO = actorDAO;
        this.directorDAO = directorDAO;
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
        GenreDAO genreDAO = new GenreDAO(emf);

        // Persist genres first to ensure they are managed
        genreDAO.persistGenres(movie.getGenres());

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

            em.persist(movie);
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
            Movie movie = em.find(Movie.class, id);
            return movie != null ? toDTO(movie) : null;
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
                    .map(actorDAO::toEntity)
                    .collect(Collectors.toList());
            movie.setCast(actors);
        }

        // Set director(s)
        if (dto.getDirectors() != null) {
            List<Director> directors = dto.getDirectors().stream()
                    .map(directorDAO::toEntity)
                    .collect(Collectors.toList());
            movie.setDirectors(directors);
        }

        // Set genres. Using a normal loop for readability, due to conversion from Integer to an object.
        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>();
            GenreDAO genreDAO = new GenreDAO(emf);

            for (Integer genreDTO : dto.getGenres()) {
                Genre genre = new Genre();
                genre.setGenre(genreDAO.getGenreNameById(genreDTO));
                genres.add(genre);
            }

            movie.setGenres(genres);
        }

        return movie;
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

        // Set cast
        if (movie.getCast() != null) {
            List<ActorDTO> actors = movie.getCast().stream()
                    .map(actorDAO::toDTO)
                    .collect(Collectors.toList());
            dto.setCast(actors);
        }

        //Set directors
        if (movie.getDirectors() != null) {
            List<DirectorDTO> directors = movie.getDirectors().stream()
                    .map(directorDAO::toDTO)
                    .collect(Collectors.toList());
            dto.setDirectors(directors);
        }

        return dto;
    }

}