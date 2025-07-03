package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.exceptions.InvalidCartException;
import uz.pdp.exceptions.InvalidCartItemException;
import uz.pdp.modul.Cart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public void add(Cart cart) throws IOException {
        carts = readCarts();
        if (!hasCart(cart.getUserId())) {
            carts.add(cart);
            save();
        } else {
            throw new InvalidCartException("Cart for this customer already exists.");
        }
    }

    @Override
    public boolean update(Cart cart, UUID id) throws Exception {
        Optional<Cart> optionalCart = getOptional(id);
        if (optionalCart.isPresent()) {
            Cart found = optionalCart.get();
            found.setPaid((Boolean) cart.isPaid());
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(UUID id) throws Exception {
        Optional<Cart> optionalCart = getOptional(id);
        if (optionalCart.isPresent()) {
            optionalCart.get().setActive(false);
            save();
            return true;
        }
        return false;
    }

    @Override
    public Cart get(UUID id) {
        return carts.stream()
                .filter(cart -> cart.isActive() && cart.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private Optional<Cart> getOptional(UUID id) {
        return carts.stream()
                .filter(cart -> cart.isActive() && cart.getId().equals(id))
                .findFirst();
    }

    public void checkout(Cart cart, ProductService productService) throws Exception {
        if (isValidCart(cart, productService)) {
            CartItemAbstract cartItem = new CartItemAbstract(cart);
            cartItem.buyItemsInCart(productService);
            remove(cart.getId());
            save();
        } else {
            throw new InvalidCartItemException("Product is out of stock or quantity is invalid.\nItem removed from cart.");
        }
    }

    private boolean isValidCart(Cart cart, ProductService productService) {
        if (cart == null || cart.isPaid() || cart.getItems() == null || cart.getItems().isEmpty()) {
            return false;
        }

        return cart.getItems().stream().allMatch(item -> {
            Product product = productService.get(item.getProductId());
            if (product != null && item.getQuantity() >= product.getQuantity()) {
                new CartItemAbstract(cart).removeItemFromCart(product);
                return false;
            }
            return true;
        });
    }

    public List<Cart> getAll() {
        return carts.stream()
                .filter(Cart::isActive)
                .collect(Collectors.toList());
    }

    public double evaluatePrice(UUID customerId, ProductService productService)
            throws InvalidCartException, IOException {
        Cart cart = Optional.ofNullable(getByCustomerId(customerId))
                .orElseThrow(() -> new InvalidCartException("Cart not found for customer: " + customerId));
        return new CartItemAbstract(cart).evaluatePrice(productService);
    }

    public Cart getByCustomerId(UUID customerId) {
        return carts.stream()
                .filter(cart -> cart.isActive() && cart.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);
    }

    public boolean hasCart(UUID customerId) {
        return carts.stream()
                .anyMatch(cart -> cart.isActive() && cart.getCustomerId().equals(customerId));
    }

    public void addItemToCart(UUID customerId, Product product, int quantity)
            throws InvalidCartException, IllegalArgumentException, IOException {
        Cart cart = Optional.ofNullable(getByCustomerId(customerId))
                .orElseThrow(() -> new InvalidCartException("Cart not found for customer: " + customerId));

        new CartItemAbstract(cart).addItemToCart(product, quantity);
        save();
    }

    public void updateItemInCart(UUID customerId, Product product, int quantity)
            throws InvalidCartException, IllegalArgumentException, IOException {
        Cart cart = Optional.ofNullable(getByCustomerId(customerId))
                .orElseThrow(() -> new InvalidCartException("Cart not found for customer: " + customerId));

        new CartItemAbstract(cart).updateItemInCart(product, quantity);
        save();
    }

    public void removeItemFromCart(Cart cart, Product product)
            throws InvalidCartException, IOException {
        new CartItemAbstract(cart).removeItemFromCart(product);
        save();
    }

    public void removeByCustomerId(UUID customerId) throws Exception {
        Cart cart = getByCustomerId(customerId);
        if (cart != null) {
            remove(cart.getId());
        }
    }

    private void save() throws IOException {
        FileUtils.writeToJson(FILE_NAME, carts);
    }

    private List<Cart> readCarts() throws IOException {
        return FileUtils.readFromJson(FILE_NAME, Cart.class);
    }

    public void clear() throws IOException {
        carts = new ArrayList<>();
        save();
    }

    public String toPrettyString(List<Cart> carts, UserService userService) {
        return carts.stream()
                .filter(Cart::isActive)
                .map(cart -> {
                    StringBuilder sb = new StringBuilder();
                    User customer = userService.get(cart.getCustomerId());
                    sb.append(customer.getUsername()).append(", ")
                            .append(CartUtils.toPrettyStringItems(cart));
                    try {
                        sb.append("Total: $").append(CartUtils.calculatePrice(cart));
                    } catch (InvalidCartException | IOException e) {
                        sb.append("Error calculating price: ").append(e.getMessage());
                    }
                    sb.append("\n");
                    return sb.toString();
                })
                .collect(Collectors.joining());
    }
}

