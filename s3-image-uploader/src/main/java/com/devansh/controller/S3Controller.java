package com.devansh.controller;

import com.devansh.exception.UserException;
import com.devansh.model.User;
import com.devansh.response.FileDataDto;
import com.devansh.service.ImageUploaderService;
import com.devansh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class S3Controller {

    private final ImageUploaderService imageUploaderService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
                                              @RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findByJwtToken(token);
        return ResponseEntity.ok(imageUploaderService.uploadImage(file, user));
    }

    @GetMapping
    public ResponseEntity<List<FileDataDto>> getAllFiles(@RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findByJwtToken(token);
        return ResponseEntity.ok(imageUploaderService.allFiles(user));
    }

    @GetMapping("/{filename}")
    public ResponseEntity<String> getImage(@PathVariable String filename) {
        return ResponseEntity.ok(imageUploaderService.getImageUrlByName(filename));
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deleteImage(@PathVariable String filename, @RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findByJwtToken(token);
        try {
            imageUploaderService.deleteImageByKey(filename, user);
            return ResponseEntity.ok("File Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
