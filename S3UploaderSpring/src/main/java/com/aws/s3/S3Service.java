package com.aws.s3;

import com.aws.users.User;
import com.aws.users.UserRepository;
import com.aws.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final UserRepository userRepository;

    @Value("${s3.bucketName}")
    private String bucketName;

    public DocResponse uploadFile(MultipartFile file, Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        try {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            String key = userId + "/" + file.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, tempFile.toPath());
            System.out.println("Put Object Response: " + response.toString());
            Files.deleteIfExists(tempFile.toPath());

            saveKeyToUser(userId, key);
            return DocResponse.builder()
                    .key("File uploaded successfully with key: " + key)
                    .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error while uploading file", e);
        }
    }

    public DocResponse uploadFileStream(MultipartFile file, Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        try (InputStream inputStream = file.getInputStream()) {
            String key = userId + "/" + file.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            System.out.println("Put Object Response (stream): " + response.toString());

            saveKeyToUser(userId, key);
            return DocResponse.builder()
                    .key("File uploaded successfully with key: " + key)
                    .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error while uploading file stream", e);
        }
    }

    public String getPreSignedUrl(String key) {
        System.out.println("Key received: " + key);
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .getObjectRequest(getObjectRequest)
                    .build();
            URL url = s3Presigner.presignGetObject(getObjectPresignRequest).url();
            return url.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error while getting pre-signed URL", e);
        }
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(deleteObjectRequest);
        System.out.println("Delete Object Response: " + deleteObjectResponse.toString());
    }

    private void saveKeyToUser(Integer userId, String key) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));
        user.getKeys().add(key);
        userRepository.save(user);
    }


}
