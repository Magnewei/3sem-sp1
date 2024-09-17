package app.persistence.daos;

import app.dtos.MovieDTO;
import app.entities.Movie;
import app.enums.HibernateConfigState;
import app.exceptions.JpaException;
import app.persistence.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class MovieDAO implements GenericDAO<MovieDTO, Movie> {
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(HibernateConfigState.TEST);

    @Transactional
    @Override
    public void create(MovieDTO movieDTO) {
        Movie movie = toEntity(movieDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(movie);
            em.getTransaction().commit();

        } catch (Exception e) {
            throw new JpaException("Could not persist movie.");
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

        // TODO: Implement conversion from MovieDTO to Movie
        return null;
    }

    @Override
    public MovieDTO toDTO(Movie movie) {
        if (movie == null) return null;

        // TODO: Implement conversion from MovieDTO to Movie
        return null;
    }
}

