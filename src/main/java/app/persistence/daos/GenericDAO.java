package app.persistence.daos;

import java.util.List;

public interface GenericDAO<T, E> {
    void create(T type);
    void delete(T type);
    T getById(int id);
    List<T> getAll();
    void update(T type);
    E toEntity(T dto);
    T toDTO(E entity);
}
