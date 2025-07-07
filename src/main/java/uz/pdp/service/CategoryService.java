package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Category;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CategoryService implements BaseService<Category> {
    private static final String FILE_NAME = "src/main/uz.pdp/data/category.txt";
    private List<Category> categories;

    public CategoryService() {
        this.categories = load();
    }

    @Override
    public void add(Category category) throws IOException {
        category.setActive(true);
        categories.add(category);
        save();
    }

    @Override
    public boolean update(Category category, UUID id) throws IOException {
        for (Category c : categories) {
            if (c.getId().equals(id)) {
                c.setName(category.getName());
                c.setParentId(category.getParentId());
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public void remove(UUID id) throws IOException {
        for (Category c : categories) {
            if (c.getId().equals(id)) {
                c.setActive(false);
                save();
                return;
            }
        }
    }

    @Override
    public Category get(UUID id) {
        return categories.stream()
                .filter(c -> c.isActive() && c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(categories);
        }
    }

    private List<Category> load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Category>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public List<Category> getAllActive() {
        return categories.stream().filter(Category::isActive).collect(Collectors.toList());
    }
}
