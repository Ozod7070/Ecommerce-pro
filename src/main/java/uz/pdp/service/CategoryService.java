package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Category;

import java.io.*;
import java.util.*;

public class CategoryService implements BaseService<Category> {
    private static final String FILE_NAME = "src/main/uz.pdp/data/categories.txt";
    private List<Category> categories;

    public CategoryService() {
        try {
            categories = read();
        } catch (Exception e) {
            categories = new ArrayList<>();
        }
    }

    @Override
    public void add(Category category) throws Exception {
        category.setId(UUID.randomUUID());
        category.setActive(true);
        categories.add(category);
        save();
    }

    @Override
    public boolean update(Category category, UUID id) throws Exception {
        Category found = get(id);
        if (found != null) {
            found.setName(category.getName());
            found.setParentId(category.getParentId());
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(UUID id) throws Exception {
        Category found = get(id);
        if (found != null) {
            found.setActive(false);
            save();
            return true;
        }
        return false;
    }

    @Override
    public Category get(UUID id) throws Exception {
        return categories.stream()
                .filter(c -> c.isActive() && c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Category> getAll() {
        return categories;
    }

    private void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(categories);
        }
    }

    private List<Category> read() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Category>) ois.readObject();
        }
    }
}