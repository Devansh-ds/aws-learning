package com.devansh.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploaderService {

    String uploadImage(MultipartFile image);
    List<String> allFiles();
    String preSignedUrl(String filename);
    String getImageUrlByName(String filename);

}
