package uz.pdp.service;

import uz.pdp.base.BaseService;
import uz.pdp.enums.UserRole;
import uz.pdp.exceptions.InvalidUserNameException;
import uz.pdp.modul.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class UserService implements BaseService <User> {
    private static final String USER_FILE = "src/main/uz.pdp/data/user.txt";
    List<User> users =  new ArrayList<>();


    @Override
    public void add(User user) throws Exception {
        Optional<User>exsitingUser = users.stream()
                .filter(u -> u.getUserName().equals(user.getUserName()))
                .findFirst();
        if(exsitingUser.isPresent()){
            throw new InvalidUserNameException("User available! ");
        }
        users.add( user );
    }


    @Override
    public void remove(UUID id) throws Exception {
        Optional<User>found = Optional.ofNullable(get(id));
       if(found.isPresent()){
           users.remove(found.get());
       }else{
           throw new InvalidUserNameException("User not found " + id);
       }
    }

    @Override
    public boolean update(User user, UUID id) throws Exception {
        Optional<User>exsitingUserOpt = Optional.ofNullable(get(id));
        if(exsitingUserOpt.isPresent()){
            User exsitingUser = exsitingUserOpt.get();
            exsitingUser.setFullName(user.getUserName());
            exsitingUser.setUserName(user.getUserName());
            exsitingUser.setPassword(user.getPassword());
            exsitingUser.setRole(user.getRole());
            exsitingUser.setActive(user.isActive());
            return  true;
        }else{
            throw new Exception("User not found " + id);
        }
    }

    @Override
    public User get(UUID id) throws Exception {
        return users.stream()
                .filter(user -> user.isActive() && user.getId().equals(id))
                .findFirst()
                .orElseThrow(()->new RuntimeException("User not found with id" + id));
    }


    public User login(String username, String password) throws Exception {
        Optional<User>userOptional = users.stream()
                .filter(User::isActive)
                .filter(user -> user.getUserName().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
        return userOptional.orElseThrow(()->new RuntimeException("Username or password invalid! " ));
    }

    public User getByUsername(String username){
       return users.stream().filter(user -> user.isActive() && user.getUserName().equals(username))
                .findFirst().orElseThrow(()->new RuntimeException("User not found! "));
    }


    public User getUsername(UUID userId) throws Exception {
        return users.stream().filter(user -> user.isActive() && user.getId().equals(userId))
                .findFirst().orElseThrow(()->new RuntimeException("User not found! "));
    }

    public List<User>getByRole(UserRole role){
        return Collections.singletonList(users.stream().filter(user -> user.isActive() && user.getRole().equals(role))
                .findFirst().orElseThrow(() -> new RuntimeException("User not found! ")));
    }

    private boolean getUserAvailable(String username){
          return users.stream().noneMatch(user -> user.isActive() && user.getUserName().equals(username));
    }

    public List<User>getAll(){
        return users.stream().filter(user -> user.isActive())
                .collect(Collectors.toList());
    }

    private void save() throws IOException {
        List<String>lines = users.stream()
                .map(user -> user.toString())
                .collect(Collectors.toList());
        Files.write(Paths.get(USER_FILE), lines);

    }


}
