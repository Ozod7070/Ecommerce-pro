package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Category;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryService implements BaseService<Category> {
    public static final UUID ROOT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String FILE_NAME = "src/main/uz.pdp/data/categories.dat";

    private List<Category> categories;

    public CategoryService() {
        categories = readCategories();
    }

    @Override
    public void add(Category category) throws Exception {
        category.setId(UUID.randomUUID());
        categories.add(category);
        saveCategories();
    }

    @Override
    public void update(Category category, UUID id) throws Exception {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(id)) {
                category.setId(id); // eski ID saqlanadi
                categories.set(i, category);
                saveCategories();
                return;
            }
        }
        throw new Exception("Category with ID " + id + " not found");
    }

    @Override
    public boolean remove(Category category) throws Exception {
        boolean removed = categories.removeIf(c -> c.getId().equals(category.getId()));
        if (removed) {
            saveCategories();
        }
        return removed;
    }

    @Override
    public Category get(UUID id) throws Exception {
        return categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Category with ID " + id + " not found"));
    }

    public List<Category> getAll() {
        return categories;
    }

    private List<Category> readCategories() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Category>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>(); // fayl yo‘q bo‘lsa, bo‘sh ro‘yxat
        }
    }

    private void saveCategories() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(categories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
