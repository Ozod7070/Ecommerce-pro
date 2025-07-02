package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Cart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartService implements BaseService<Cart> {
    private static final String FILE_NAME = "carts.json";
    private List<Cart> carts;

    public CartService() {
        try {
            carts = readCarts();
        } catch (IOException e) {
            carts = new ArrayList<>();
        }
    }

    @Override
    public void add(Cart cart) throws Exception {

    }

    @Override
    public void update(Cart cart, UUID id) throws Exception {

    }

    @Override
    public boolean remove(Cart cart) throws Exception {
        return false;
    }

    @Override
    public Cart get(UUID id) throws Exception {
        return null;
    }
}
