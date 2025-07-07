package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Product;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProductService implements BaseService<Product> {
    private static final String FILE_NAME = "src/main/uz.pdp/data/products.txt";
    private List<Product> products;

    public ProductService() {
        try {
            products = read();
        } catch (Exception e) {
            products = new ArrayList<>();
        }
    }

    @Override
    public void add(Product product) throws Exception {
        product.setActive(true);
        products.add(product);
        save();
    }

    @Override
    public boolean update(Product product, UUID id) throws Exception {
        Product found = get(id);
        if (found != null) {
            found.setName(product.getName());
            found.setPrice(product.getPrice());
            found.setQuantity(product.getQuantity());
            found.setCategoryId(product.getCategoryId());
            found.setSellerId(product.getSellerId());
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(UUID id) throws Exception {
        Product found = get(id);
        if (found != null) {
            found.setActive(false);
            save();
            return true;
        }
        return false;
    }

    @Override
    public Product get(UUID id) throws Exception {
        return products.stream()
                .filter(p -> p.isActive() && p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Product> getByCategory(UUID categoryId) {
        return products.stream()
                .filter(p -> p.isActive() && p.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    private void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(products);
        }
    }

    private List<Product> read() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Product>) ois.readObject();
        }
    }
}