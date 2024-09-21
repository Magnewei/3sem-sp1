package app.persistence.daos;

import app.dtos.GenreDTO;
import app.entities.Genre;
import app.exceptions.JpaException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class GenreDAO implements GenericDAO<GenreDTO, Genre> {
    private final EntityManagerFactory emf;
    public GenreDAO(EntityManagerFactory entityManagerFactory) {
        emf = entityManagerFactory;
    }

    @Override
    public GenreDTO create(GenreDTO genreDTO) {
        Genre genre = toEntity(genreDTO);
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(genre);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Could not create genre." + e.getMessage() + e.getCause());
        }
        return genreDTO;
    }

    @Override
    public void delete(GenreDTO genreDTO) {
        Genre genre = toEntity(genreDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.remove(genre);
            em.getTransaction().commit();

        } catch (Exception e) {
            throw new JpaException("Could not delete actor" + e.getMessage());
        }
    }

    @Override
    public GenreDTO getById(int id) {
        Genre genre;
        try (var em = emf.createEntityManager()) {
            genre = em.find(Genre.class, id);
        } catch (Exception e) {
            throw new JpaException("Could not get genre by id." + e.getMessage());
        }
        return toDTO(genre);
    }

    @Override
    public List<GenreDTO> getAll() {
        List<Genre> genres;
        try (var em = emf.createEntityManager()) {
            genres = em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
        } catch (Exception e) {
            throw new JpaException("Could not get all genres." + e.getMessage());
        }
        return genres.stream().map(this::toDTO).toList();
    }

    @Override
    public void update(GenreDTO genreDTO) {
        Genre genre = toEntity(genreDTO);
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(genre);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Could not update genre." + e.getMessage() + e.getCause());
        }
    }

    @Override
    public Genre toEntity(GenreDTO dto) {
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setGenre(dto.getGenreName());
        genre.setMovies(dto.getMovies());
        return genre;
    }

    @Override
    public GenreDTO toDTO(Genre entity) {
        return GenreDTO.builder()
                .id(entity.getId())
                .genreName(entity.getGenre())
                .movies(entity.getMovies())
                .build();
    }

    String getGenreNameById(int id) {
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
}
