package com.devansh.service;

import com.devansh.model.User;
import com.devansh.response.FileDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploaderService {

    String uploadImage(MultipartFile image, User user);
    List<FileDataDto> allFiles(User user);
    String preSignedUrl(String filename);
    String getImageUrlByName(String filename);
    void deleteImageByKey(String key, User user);

}
