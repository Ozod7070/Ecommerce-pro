package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Cart;
import uz.pdp.modul.Product;

import java.io.*;
import java.util.*;

public class CartService implements BaseService<Cart> {
    private static final String FILE_NAME = "src/main/uz.pdp/data/carts.txt";
    private List<Cart> carts;

    public CartService() {
        try {
            carts = read();
        } catch (Exception e) {
            carts = new ArrayList<>();
        }
    }

    @Override
    public void add(Cart cart) throws Exception {
        cart.setId(UUID.randomUUID());
        cart.setActive(true);
        carts.add(cart);
        save();
    }

    @Override
    public boolean update(Cart cart, UUID id) throws Exception {
        Cart found = get(id);
        if (found != null) {
            found.setPaid(cart.isPaid());
            found.setItems(cart.getItems());
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(UUID id) throws Exception {
        Cart found = get(id);
        if (found != null) {
            found.setActive(false);
            save();
            return true;
        }
        return false;
    }

    @Override
    public Cart get(UUID id) throws Exception {
        return carts.stream()
                .filter(c -> c.isActive() && c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addItem(UUID cartId, Product product, int quantity) throws Exception {
        Cart cart = get(cartId);
        if (cart != null) {
            cart.getItems().add(new Cart.Item(product.getId(), quantity));
            save();
        }
    }

    public void clear() throws IOException {
        carts.clear();
        save();
    }

    private void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(carts);
        }
    }

    private List<Cart> read() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Cart>) ois.readObject();
        }
    }
}
