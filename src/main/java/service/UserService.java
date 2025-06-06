package service;

import java.util.List;

import models.User;
import repository.UserRepository;
import utils.SecurityUtil;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register a new user
    public boolean register(String username, String password, String role) {
        if (userRepository.findUserByUsername(username) != null) {
            System.out.println("Username already exists!");
            return false;
        }
        User user = new User(username, password, role);
        return userRepository.registerUser(user);
    }

    // Authenticate login
    public User login(String username, String password) {
        User user = userRepository.findUserByUsername(username);
        if (user != null && SecurityUtil.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // List all users
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
