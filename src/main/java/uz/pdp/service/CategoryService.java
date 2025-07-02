package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.Category;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CategoryService implements BaseService<Category> {
    public static final UUID ROOT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String FILE_NAME = "src/main/uz.pdp/data/categories.dat";

    private List<Category> categories;

    public CategoryService() {
        categories = readCategories();
    }

    @Override
    public void add(Category category) throws Exception {
        if (isCategoryValid(category)) {
            category.setId(UUID.randomUUID());
            category.setActive(true);
            categories.add(category);
            saveCategories();
        } else {
            throw new Exception("Category with this name already exists or is invalid.");
        }
    }

    @Override
    public boolean update(Category category, UUID id) throws Exception {
        boolean updated = categories.stream()
                .filter(c -> c.isActive() && c.getId().equals(id))
                .peek(c -> c.setName(category.getName()))
                .findFirst()
                .isPresent();
        if (updated) saveCategories();
        return updated;
    }

    @Override
    public boolean remove(Category category) throws Exception {
        boolean removed = categories.stream()
                .filter(c -> c.getId().equals(category.getId()))
                .peek(c -> c.setActive(false))
                .findFirst()
                .isPresent();
        if (removed) saveCategories();
        return removed;
    }

    @Override
    public Category get(UUID id) throws Exception {
        return categories.stream()
                .filter(c -> c.isActive() && c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Category with ID " + id + " not found"));
    }

    public List<Category> getAll() {
        return categories.stream()
                .filter(Category::isActive)
                .collect(Collectors.toList());
    }

    public boolean isLast(UUID categoryId) {
        return categories.stream()
                .filter(c -> c.isActive() && c.getParentId().equals(categoryId))
                .findAny()
                .isEmpty();
    }

    public Category getByName(String name) {
        return categories.stream()
                .filter(c -> c.isActive() && c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Category> getChildren(UUID categoryId) {
        return categories.stream()
                .filter(c -> c.isActive() && c.getParentId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<Category> getChildren(String name) {
        UUID parentId = name.equalsIgnoreCase("Root") ? ROOT_UUID :
                getByName(name) != null ? getByName(name).getId() : null;
        if (parentId == null) return new ArrayList<>();
        return getChildren(parentId);
    }

    public List<Category> getLastCategories() {
        return categories.stream()
                .filter(c -> c.isActive() && isLast(c.getId()))
                .collect(Collectors.toList());
    }

    public void killChildren(UUID categoryId) throws IOException {
        List<Category> children = getChildren(categoryId);
        if (children.isEmpty()) return;

        children.forEach(child -> {
            child.setActive(false);
            try {
                killChildren(child.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        saveCategories();
    }

    public void remove(UUID id) throws IOException {
        categories.stream()
                .filter(c -> c.isActive() && c.getId().equals(id))
                .findFirst()
                .ifPresent(c -> {
                    c.setActive(false);
                    try {
                        killChildren(c.getId());
                        saveCategories();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean isCategoryValid(Category category) {
        return categories.stream()
                .noneMatch(c -> c.isActive() &&
                        c.getName().equalsIgnoreCase(category.getName()));
    }

    private List<Category> readCategories() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Category>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveCategories() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(categories);
        }
    }

    public void clear() throws IOException {
        categories.clear();
        saveCategories();
    }

    public String toPrettyString(List<Category> list) {
        return list.stream()
                .map(Category::getName)
                .collect(Collectors.joining("\n"));
    }
}
