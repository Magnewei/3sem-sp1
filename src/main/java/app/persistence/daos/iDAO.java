package app.persistence.daos;

import java.util.Set;

public interface iDAO<T> {
    boolean create(T type);
    boolean delete(T type);
    T getById(int id);
    Set<T> getAll();
    boolean update(T student);
}
