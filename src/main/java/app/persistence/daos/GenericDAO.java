package app.persistence.daos;

import java.util.List;
import java.util.Set;

public interface GenericDAO<T> {
    boolean create(T type);
    boolean delete(T type);
    T getById(int id);
    List<T> getAll();
    boolean update(T student);
}
