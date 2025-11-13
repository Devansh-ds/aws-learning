package com.aws.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserDto request) {
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        return userRepository.save(user);
    }

    public User getUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(UserDto request, Integer id) {
        User user = getUser(id);
        user.setEmail(request.email());
        user.setPassword(request.password());
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.delete(getUser(id));
    }
}
