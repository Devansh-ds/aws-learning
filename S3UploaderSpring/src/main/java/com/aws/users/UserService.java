package com.aws.users;

import com.aws.s3.DocResponse;
import com.aws.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

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

    public DocResponse uploadFile(Integer userId, MultipartFile file) {
        return s3Service.uploadFile(file, userId);
    }

    public DocResponse uploadFileStream(Integer userId, MultipartFile file) {
        return s3Service.uploadFileStream(file, userId);
    }

    public String getPresignedUrl(String key) {
        return s3Service.getPreSignedUrl(key);
    }

    public List<String> getAllUrls(Integer userId) {
        User user = findUserById(userId);
        List<String> keys = user.getKeys();
        List<String> urls = new ArrayList<>();
        for (String key : keys) {
            urls.add(getPresignedUrl(key));
        }
        return urls;
    }

    public void deleteFile(String key) {
        s3Service.deleteFile(key);
    }
}
