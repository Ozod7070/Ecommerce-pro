
package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Product;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProductService implements BaseService<Product> {
    private static final String PRODUCT_FILE = "src/main/uz.pdp/data/product.txt";
    private static List<Product> products = new ArrayList<>();

    @Override
    public void add(Product product) throws IOException {
        product.setId(UUID.randomUUID());
        product.setActive(true);
        products.add(product);
        writeProducts();
    }

    @Override
    public boolean update(Product product, UUID id) throws IOException {
        Product p = get(id);
        if (p != null) {
            p.setName(product.getName());
            p.setPrice(product.getPrice());
            p.setQuantity(product.getQuantity());
            p.setCategoryId(product.getCategoryId());
            p.setSellerId(product.getSellerId());
            writeProducts();
            return true;
        }
        return false;
    }

    @Override
    public void remove(UUID id) throws IOException {
        products.removeIf(p -> p.getId().equals(id));
        writeProducts();
    }

    @Override
    public Product get(UUID id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void writeProducts() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PRODUCT_FILE))) {
            out.writeObject(products);
        }
    }
}