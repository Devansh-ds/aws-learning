package com.aws.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Integer userId, User user) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        oldUser.setUsername(user.getUsername());
        return userRepository.save(oldUser);
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    public User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}
