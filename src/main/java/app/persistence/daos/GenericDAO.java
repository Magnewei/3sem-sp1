package app.persistence.daos;

import java.util.List;
import java.util.Set;

public interface GenericDAO<T, E> {
    void create(T type);
    void delete(T type);
    T getById(int id);
    List<T> getAll();
    void update(T type);
    E toEntity(T dto);
    T toDTO(E entity);
}
