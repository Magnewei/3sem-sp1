package app.persistence.daos;

import app.dtos.MoviePersonDTO;
import app.entities.MoviePerson;
import app.enums.HibernateConfigState;
import app.exceptions.JpaException;
import app.persistence.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class MoviePersonDAO implements GenericDAO<MoviePersonDTO, MoviePerson> {
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(HibernateConfigState.TEST);

    public MoviePersonDAO(EntityManagerFactory emf) {
        MoviePersonDAO.emf = emf;
    }

    @Override
    public void create(MoviePersonDTO moviePersonDTO) {
        MoviePerson moviePerson = toEntity(moviePersonDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(moviePerson);
            em.getTransaction().commit();

        } catch (Exception e) {
            throw new JpaException("Could not persist movie person.");
        }
    }

    @Override
    public void delete(MoviePersonDTO moviePersonDTO) {
        MoviePerson moviePerson = toEntity(moviePersonDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.remove(moviePerson);
            em.getTransaction().commit();

        } catch (Exception e) {
            throw new JpaException("Could not delete movie person");
        }
    }

    @Override
    public MoviePersonDTO getById(int id) {

        try (var em = emf.createEntityManager()) {
            MoviePerson moviePerson = em.createQuery("SELECT m FROM MoviePerson m WHERE m.id = :id", MoviePerson.class)
                    .setParameter("id", id)
                    .getSingleResult();

            return toDTO(moviePerson);
        } catch (Exception e) {
            throw new JpaException("Could not get movie person by id.");
        }
    }

    @Override
    public List<MoviePersonDTO> getAll() {
        try (var em = emf.createEntityManager()) {
            List<MoviePerson> moviePersons = em.createQuery("SELECT m FROM MoviePerson m", MoviePerson.class)
                    .getResultList();

            return moviePersons.stream()
                    .map(this::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new JpaException("Could not get all movie persons.");
        }
    }

    @Override
    public void update(MoviePersonDTO type) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(type);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Could not update movie person.");
        }
    }

    @Override
    public MoviePerson toEntity(MoviePersonDTO dto) {
        return null;
    }

    @Override
    public MoviePersonDTO toDTO(MoviePerson entity) {
        return null;
    }
}
