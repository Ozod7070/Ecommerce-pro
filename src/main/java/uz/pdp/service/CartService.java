package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Cart;
import uz.pdp.modul.Product;
import uz.pdp.modul.User;

import java.io.*;
import java.util.*;

public class CartService implements BaseService<Cart> {
    private static final String FILE_NAME = "carts.txt";
    private List<Cart> carts;

    public CartService() {
        try {
            carts = readCarts();
        } catch (Exception e) {
            carts = new ArrayList<>();
        }
    }

    @Override
    public void add(Cart cart) throws IOException {
        cart.setId(UUID.randomUUID());
        cart.setActive(true);
        carts.add(cart);
        save();
    }

    @Override
    public boolean update(Cart cart, UUID id) throws IOException {
        Cart c = get(id);
        if (c != null) {
            c.setPaid(cart.isPaid());
            save();
            return true;
        }
        return false;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Cart c = get(id);
        if (c != null) {
            c.setActive(false);
            save();
        }
    }

    @Override
    public Cart get(UUID id) {
        return carts.stream()
                .filter(c -> c.isActive() && c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addItem(UUID customerId, Product product, int quantity) throws IOException {
        Cart cart = getByCustomerId(customerId);
        if (cart == null) return;
        cart.getItems().add(new Cart.Item(product.getId(), quantity));
        save();
    }

    public void removeItem(Cart cart, Product product) throws IOException {
        cart.getItems().removeIf(item -> item.getProductId().equals(product.getId()));
        save();
    }

    public Cart getByCustomerId(UUID customerId) {
        return carts.stream()
                .filter(c -> c.isActive() && c.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);
    }

    public double calculateTotalPrice(Cart cart, ProductService productService) {
        double total = 0;
        for (Cart.Item item : cart.getItems()) {
            Product p = productService.get(item.getProductId());
            if (p != null) {
                total += p.getPrice() * item.getQuantity();
            }
        }
        return total;
    }

    public String toPrettyString(List<Cart> carts, UserService userService, ProductService productService) {
        StringBuilder sb = new StringBuilder();
        for (Cart cart : carts) {
            if (cart.isActive()) {
                User user = userService.get(cart.getCustomerId());
                sb.append(user.getUserName()).append(", ");
                for (Cart.Item item : cart.getItems()) {
                    sb.append("Product ID: ").append(item.getProductId())
                            .append(", Quantity: ").append(item.getQuantity()).append(" | ");
                }
                sb.append("Total: $").append(calculateTotalPrice(cart, productService)).append("\n");
            }
        }
        return sb.toString();
    }

    private void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(carts);
        }
    }

    private List<Cart> readCarts() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Cart>) ois.readObject();
        }
    }
}
