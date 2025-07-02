package uz.pdp.base;

import uz.pdp.exceptions.InvalidUserNameException;
import uz.pdp.modul.User;

import java.util.UUID;

public interface BaseService<T>{


    void add(T t) throws Exception;


    void update(T t, UUID id) throws Exception;


    boolean remove(UUID id) throws Exception;


    T get(UUID id) throws Exception;
}
