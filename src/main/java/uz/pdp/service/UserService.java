package uz.pdp.service;

import uz.pdp.base.BaseService;

import java.util.UUID;

public class  UserService implements BaseService {
    public UserService() {
        super();
    }

    @Override
    public void add(Object o) throws Exception {

    }

    @Override
    public boolean update(Object o, UUID id) throws Exception {

        return false;
    }

    @Override
    public boolean remove(Object o) throws Exception {
        return false;
    }

    @Override
    public Object get(UUID id) throws Exception {
        return null;
    }
}
