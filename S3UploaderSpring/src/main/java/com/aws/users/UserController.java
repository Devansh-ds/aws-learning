package com.aws.users;

import com.aws.s3.DocResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Integer userId, @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/{userId}/upload")
    public DocResponse uploadDoc(
            @PathVariable Integer userId,
            @RequestPart MultipartFile file
            ) {
        return userService.uploadFile(userId, file);
    }

    @PostMapping("/{userId}/upload-stream")
    public DocResponse uploadDocStream(
            @PathVariable Integer userId,
            @RequestPart MultipartFile file
    ) {
        return userService.uploadFileStream(userId, file);
    }

    @GetMapping("/key")
    public String getDocUrl(@RequestParam String key) {
        return userService.getPresignedUrl(key);
    }

    @GetMapping("/{userId}/key/all")
    public List<String> getAllDocUrls(@PathVariable Integer userId) {
        return userService.getAllUrls(userId);
    }

    @DeleteMapping("/key")
    public void deleteDoc(@RequestParam String key) {
        userService.deleteFile(key);
    }

}

















