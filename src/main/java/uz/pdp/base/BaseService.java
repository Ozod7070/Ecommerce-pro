package uz.pdp.base;

import uz.pdp.modul.Category;

import java.util.UUID;

public interface BaseService<T>{


    void add(T t) throws Exception;

    boolean update(T t, UUID id) throws Exception;

    void remove(UUID id) throws Exception;

    T get(UUID id) throws Exception;
}
