package uz.pdp.base;

import java.util.UUID;

public interface BaseService<T>{


    void add(T t) throws Exception;

    boolean update(T t, UUID id) throws Exception;

    boolean remove(UUID id) throws Exception;

    T get(UUID id) throws Exception;
}
