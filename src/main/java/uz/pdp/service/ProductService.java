package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductService implements BaseService<Product> {
    private static final String PRODUCT_FILE = "src/main/uz.pdp/data/product.txt";
    private static  List<Product> products = new ArrayList<>();

    public Optional<List<Product>> readProducts() {
        File file = new File(PRODUCT_FILE);
        if (!file.exists() || file.length() == 0)
            return Optional.empty();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<Product> list = (List<Product>) in.readObject();
            return Optional.of(list);
        } catch (Exception e) {
            System.err.println("readProducts error: " + e.getMessage());
            return Optional.empty();
        }
    }


    public static void writeProducts() throws IOException, ClassNotFoundException {
        try (ObjectOutput output = new ObjectOutputStream(new FileOutputStream(PRODUCT_FILE))) {
            output.writeObject(products);
            output.close();
        }catch (Exception e) {
            System.err.println("writeProducts() error" + e.getMessage());
        }
    }

    @Override
    public void add(Product product) throws Exception {
       if (isProductValid(product)) {
           Product product2 = getByName(product.getName());
           if (product2 != null) {
               product2.setQuantity(product2.getQuantity() + product.getQuantity());
           }else {
               products.add(product);
           }
           writeProducts();
       }else {
           System.err.println("Invalid product");
       }
    }

    @Override
    public boolean update(Product product, UUID id) throws Exception {
            Product product2 = get(id);
            if (product2 != null) {
                product2.setName(product.getName());
                product2.setPrice(product.getPrice());
                product2.setQuantity(product.getQuantity());
                product2.setCategoryId(product.getCategoryId());
                product2.setSellerId(product.getSellerId());
                product2.setSellerId(product.getSellerId());
                writeProducts();
                return true;
            }
            return false;
    }

    @Override
    public void remove(Product product) throws Exception {
        boolean removed = products.removeIf(p -> p.getId().equals(product.getId()));
        if (removed) {
            writeProducts();
        }
        return removed;
    }

    @Override
    public Product get(UUID id) throws Exception {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean isProductValid(Product product) {
        return product.getPrice() > 0 && product.getQuantity() > 0;
    }

    public Product getByName(String name) {
        return products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Product> getByCategoryId(UUID categoryId) {
        return products.stream()
                .filter(p -> p.isActive() && p.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<Product> getBySellerId(UUID sellerId) {
       return products.stream()
                .filter(p -> p.isActive() && p.getSellerId().equals(sellerId))
                .collect(Collectors.toList());
    }

    public boolean isCategoryEmpty(UUID categoryId) {
        return products.stream()
                .filter(p -> p.isActive() && p.getCategoryId().equals(categoryId))
                .findFirst()
                .isEmpty();
    }

    public void removeProduct(UUID categoryId) throws IOException, ClassNotFoundException {
        for (Product product : products) {
            if (product.isActive() && product.getCategoryId().equals(categoryId)) {
                product.setActive(false);
            }
        }
        writeProducts();
    }

    public void byProduct(UUID productId,int quantity) throws Exception {
        Product product = get(productId);
        if (product == null || !product.isActive()) {
            System.err.println("product not found");
        }
        product.setQuantity(product.getQuantity() - quantity);
        if (product.getQuantity() <= 0) {
            product.setActive(false);
        }
        writeProducts();
    }

   public String toProductstring() {
        StringBuilder sb = new StringBuilder();
        for (Product product : products) {
            sb.append(product.toString());
        }
        return sb.toString();
   }
}
