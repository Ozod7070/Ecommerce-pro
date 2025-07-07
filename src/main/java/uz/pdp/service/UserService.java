package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.User;

import java.util.UUID;

public class UserService implements BaseService<User> {
    @Override
    public void add(User user) {}

    @Override
    public boolean update(User user, UUID id) { return false; }

    @Override
    public void remove(UUID id) {}

    @Override
    public User get(UUID id) {
        return new User();
    }
}