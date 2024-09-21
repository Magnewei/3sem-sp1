package app.persistence.daos;

import app.dtos.ActorDTO;
import app.dtos.MovieDTO;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.exceptions.JpaException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class ActorDAO implements GenericDAO<ActorDTO, Actor> {
    private final EntityManagerFactory emf;
    public ActorDAO(EntityManagerFactory entityManagerFactory) {
        emf = entityManagerFactory;
    }

    @Override
    public ActorDTO create(ActorDTO actorDTO) {
        Actor actor = toEntity(actorDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(actor);
            em.getTransaction().commit();

            actorDTO.setId(actor.getId());
        } catch (EntityExistsException e) {
            throw new JpaException("Actor already exists." + e.getMessage());
        } catch (Exception e) {
            throw new JpaException("Could not create actor." + e.getMessage() + e.getCause());
        }
        return actorDTO;
    }

    @Override
    public void delete(ActorDTO actorDTO) {
        Actor actor = toEntity(actorDTO);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.remove(actor);
            em.getTransaction().commit();

        } catch (Exception e) {
            throw new JpaException("Could not delete actor" + e.getMessage());
        }
    }

    @Override
    public ActorDTO getById(int id) {
        Actor actor;
        try (var em = emf.createEntityManager()) {
            actor = em.find(Actor.class, id);
        } catch (Exception e) {
            throw new JpaException("Could not get actor by id" + e.getMessage());
        }
        return toDTO(actor);
    }

    @Override
    public List<ActorDTO> getAll() {
        List<Actor> actors;
        try (var em = emf.createEntityManager()) {
            actors = em.createQuery("SELECT a FROM Actor a", Actor.class).getResultList();
        } catch (Exception e) {
            throw new JpaException("Could not get all actors" + e.getMessage());
        }
        return actors.stream().map(this::toDTO).toList();
    }

    @Override
    public void update(ActorDTO actorDTO) {
        Actor actor = toEntity(actorDTO);
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(actor);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Could not update actor" + e.getMessage());
        }
    }

    @Override
    public Actor toEntity(ActorDTO dto) {
        Actor actor = new Actor();
        actor.setId(dto.getId());
        actor.setName(dto.getName());
        actor.setGender(dto.getGender());
        //avoiding recursion
        actor.setKnownFor(new ArrayList<>());
        return actor;
    }

    @Override
    public ActorDTO toDTO(Actor actor) {
        ActorDTO actorDTO = new ActorDTO();
        actorDTO.setId(actor.getId());
        actorDTO.setName(actor.getName());
        actorDTO.setGender(actor.getGender());
        //avoiding recursion
        actorDTO.setKnownFor(new ArrayList<>());

        return actorDTO;
    }

}
