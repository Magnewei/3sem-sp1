package app.persistence.daos;

import java.util.List;

public interface GenericDAO<T, E> {
    T create(T type);
    void delete(T type);
    T getById(int id);
    List<T> getAll();
    void update(T type);
    E toEntity(T dto);
    T toDTO(E entity);
}
