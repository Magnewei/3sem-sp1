package app.persistence.daos;

import app.dtos.DirectorDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.JpaException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class DirectorDAO implements GenericDAO<DirectorDTO, Director> {
    private final EntityManagerFactory emf;
    public DirectorDAO(EntityManagerFactory entityManagerFactory) {
        emf = entityManagerFactory;
    }

    @Override
    public DirectorDTO create(DirectorDTO directorDTO) {
        Director director = toEntity(directorDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(director);
            em.getTransaction().commit();

            directorDTO.setId(director.getId());
        } catch (EntityExistsException e) {
            throw new JpaException("Director already exists." + e.getMessage());
        } catch (Exception e) {
            throw new JpaException("Could not create director." + e.getMessage() + e.getCause());
        }
        return directorDTO;
    }

    @Override
    public void delete(DirectorDTO directorDTO) {
        Director director = toEntity(directorDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.remove(director);
            em.getTransaction().commit();

        } catch (Exception e) {
            throw new JpaException("Could not delete director" + e.getMessage());
        }
    }

    @Override
    public DirectorDTO getById(int id) {
        Director director;
        try (var em = emf.createEntityManager()) {
            director = em.find(Director.class, id);
        } catch (Exception e) {
            throw new JpaException("Could not get director by id" + e.getMessage());
        }
        return toDTO(director);
    }

    @Override
    public List<DirectorDTO> getAll() {
        List<Director> directors;
        try (var em = emf.createEntityManager()) {
            directors = em.createQuery("SELECT d FROM Director d", Director.class).getResultList();
        } catch (Exception e) {
            throw new JpaException("Could not get all actors" + e.getMessage());
        }
        return directors.stream().map(this::toDTO).toList();
    }

    @Override
    public void update(DirectorDTO directorDTO) {
        Director director = toEntity(directorDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(director);
            em.getTransaction().commit();

        } catch (Exception e) {
            throw new JpaException("Could not update director." + e.getMessage());
        }
    }

    @Override
    public Director toEntity(DirectorDTO dto) {

        Director director = new Director();
        director.setId(dto.getId());
        director.setName(dto.getName());
        director.setGender(dto.getGender());
        //avoiding recursion
        director.setKnownFor(new ArrayList<>());

        return director;
    }

    @Override
    public DirectorDTO toDTO(Director director) {
        DirectorDTO directorDTO = new DirectorDTO();
        directorDTO.setId(director.getId());
        directorDTO.setName(director.getName());
        directorDTO.setGender(director.getGender());
        //avoiding recursion
        directorDTO.setKnownFor(new ArrayList<>());

        return directorDTO;
    }
}
