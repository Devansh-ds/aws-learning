package com.devansh.controller;

import com.devansh.service.ImageUploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class S3Controller {

    private final ImageUploaderService imageUploaderService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(imageUploaderService.uploadImage(file));
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllImages() {
        return ResponseEntity.ok(imageUploaderService.allFiles());
    }

    @GetMapping("/{filename}")
    public ResponseEntity<String> getImage(@PathVariable String filename) {
        return ResponseEntity.ok(imageUploaderService.getImageUrlByName(filename));
    }

}
