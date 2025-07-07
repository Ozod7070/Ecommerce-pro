package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.modul.User;

import java.util.*;
import java.io.*;

public class UserService implements BaseService<User> {
    private static final String USER_FILE = "src/main/uz.pdp/data/users.txt";
    private List<User> users;

    public UserService() {
        try {
            users = readUsers();
        } catch (Exception e) {
            users = new ArrayList<>();
        }
    }

    @Override
    public void add(User user) throws Exception {
        user.setId(UUID.randomUUID());
        user.setActive(true);
        users.add(user);
        save();
    }

    @Override
    public boolean update(User user, UUID id) throws Exception {
        User existing = get(id);
        if (existing != null) {
            existing.setFullName(user.getFullName());
            existing.setUserName(user.getUserName());
            existing.setPassword(user.getPassword());
            existing.setRole(user.getRole());
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(UUID id) throws Exception {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) save();
        return removed;
    }

    @Override
    public User get(UUID id) throws Exception {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAll() {
        return users;
    }

    public void clear() throws IOException {
        users.clear();
        save();
    }

    public String toPrettyString(List<User> list) {
        StringBuilder sb = new StringBuilder();
        for (User user : list) {
            sb.append(user.getUserName()).append(" - ").append(user.getRole()).append("\n");
        }
        return sb.toString();
    }

    private void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        }
    }

    private List<User> readUsers() throws IOException, ClassNotFoundException {
        File file = new File(USER_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        }
    }
}
