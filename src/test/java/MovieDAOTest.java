import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Movie;
import app.enums.HibernateConfigState;
import app.persistence.HibernateConfig;
import app.persistence.daos.MovieDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MovieDAOTest {
    private static MovieDAO movieDAO;
    private static EntityManagerFactory emfTest;
    private static EntityManager entityManager;

    private static MovieDTO movieDTO;

    @BeforeAll
    static void setUpAll() {
        emfTest = HibernateConfig.getEntityManagerFactoryConfig(HibernateConfigState.TEST);
        movieDAO = new MovieDAO(emfTest);
        entityManager = emfTest.createEntityManager();
        movieDTO = MovieDTO.builder()
                .originalTitle("Test Movie")
                .releaseDate(LocalDate.of(2023, 9, 20))
                .voteAverage(8.5)
                .cast(List.of(ActorDTO.builder().name("Test Actor").gender(1).build()))
                .directors(List.of(DirectorDTO.builder().name("Test Director").gender(1).build()))
                .build();
        movieDAO.create(movieDTO);

    }

    @AfterAll
    public static void tearDown() {
        movieDAO.delete(movieDTO);
        entityManager.close();
        emfTest.close();
    }

    @Test
    public void testCreateMovie() {
        MovieDTO movieDTO = MovieDTO.builder()
                .originalTitle("Test Movie")
                .releaseDate(LocalDate.of(2023, 9, 20))
                .voteAverage(8.5)
                .cast(List.of(ActorDTO.builder().name("Test Actor").gender(1).build()))
                .directors(List.of(DirectorDTO.builder().name("Test Director").gender(1).build()))
                .build();

        // Persist the movie and get the updated MovieDTO with ID
        MovieDTO persistedMovieDTO = movieDAO.create(movieDTO);

        entityManager.getTransaction().begin();
        MovieDTO getDtoFromID = movieDAO.getById(persistedMovieDTO.getId());
        Movie retrievedMovie = movieDAO.toEntity(getDtoFromID);
        entityManager.getTransaction().commit();

        assertNotNull(retrievedMovie, "Movie should be persisted and retrievable.");
        assertEquals("Test Movie", retrievedMovie.getOriginalTitle());
        assertEquals(LocalDate.of(2023, 9, 20), retrievedMovie.getReleaseDate());
        assertEquals(8.5, retrievedMovie.getVoteAverage());
    }

    @Test
    public void testGetAllMovies() {
        List<MovieDTO> movies = movieDAO.getAll();
        assertNotNull(movies, "Movie list should not be null.");
        assertFalse(movies.isEmpty(), "Movie list should not be empty.");
    }

    @Test
    public void testGetMovieById() {
        MovieDTO movieDTO = MovieDTO.builder()
                .originalTitle("Another Test Movie")
                .releaseDate(LocalDate.of(2024, 1, 1))
                .voteAverage(7.0)
                .build();

        movieDAO.create(movieDTO);

        MovieDTO retrievedMovie = movieDAO.getById(movieDTO.getId());

        assertNotNull(retrievedMovie, "Retrieved movie should not be null.");
        assertEquals(movieDTO.getOriginalTitle(), retrievedMovie.getOriginalTitle());
    }

    @Test
    public void testDeleteMovie() {
        MovieDTO movieDTO = MovieDTO.builder()
                .originalTitle("Test Delete")
                .releaseDate(LocalDate.of(2023, 9, 20))
                .voteAverage(8.5)
                .build();

        // Persist the movie
        MovieDTO persistedMovieDTO = movieDAO.create(movieDTO);

        // Delete the movie
        entityManager.getTransaction().begin();
        movieDAO.delete(persistedMovieDTO);
        entityManager.getTransaction().commit();

        // Refetch to check deletion
        MovieDTO deletedMovie = movieDAO.getById(persistedMovieDTO.getId());

        assertNull(deletedMovie, "Movie should be null after deletion.");
    }


    // TODO: Implement the following tests
    @Test
    void persistGenres() {
    }

    @Test
    void getTotalAverageRating() {
    }

    @Test
    void getTop10LowestRatedMovies() {
    }

    @Test
    void getTop10HighestRatedMovies() {
    }

    @Test
    void getMoviesByTitle() {
    }
}