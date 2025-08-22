package com.devansh.service.impl;

import com.devansh.exception.ImageUploaderException;
import com.devansh.model.FileMetadata;
import com.devansh.model.User;
import com.devansh.repo.FileMetadataRepository;
import com.devansh.repo.UserRepository;
import com.devansh.response.FileDataDto;
import com.devansh.service.ImageUploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploaderService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final UserRepository userRepository;
    private final FileMetadataRepository fileMetadataRepository;

    @Value("${app.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadImage(MultipartFile image, User user) {

        if (image == null || image.isEmpty()) {
            throw new ImageUploaderException("Image is null or empty");
        }

        long fileSize = image.getSize();

        // Reset daily count if date is changed
        if (user.getLastUploadDate() != null || user.getLastUploadDate().equals(LocalDate.now())) {
            user.setLastUploadDate(LocalDate.now());
            user.setDailyUploadCount(0);
        }

        // check if user already upload day limited files
        if (user.getDailyUploadCount() >= 10) {
            throw new ImageUploaderException("Daily upload limit exceeded (10 files per day)");
        }

        if (user.getUsedStorage() + fileSize > user.getStorageQuota()) {
            throw new ImageUploaderException("You exceeded storage limit of 40 MB");
        }

        // upload file and save info in User and FileMetadata
        try {
            String originalFilename = image.getOriginalFilename();
            String newFilename = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

            s3Client.putObject(PutObjectRequest.builder()
                    .key(newFilename)
                    .bucket(bucketName)
                    .build(), RequestBody.fromBytes(image.getBytes()));

            // save upload info in user and file metadata
            user.setDailyUploadCount(user.getDailyUploadCount() + 1);
            user.setUsedStorage(user.getUsedStorage() + fileSize);
            user.setLastUploadDate(LocalDate.now());

            userRepository.save(user);

            FileMetadata fileMetadata = FileMetadata.builder()
                    .key(newFilename)
                    .contentType(image.getContentType())
                    .originalFilename(originalFilename)
                    .size(image.getSize())
                    .uploadedBy(user)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            fileMetadataRepository.save(fileMetadata);

            return preSignedUrl(newFilename);
        } catch (Exception e) {
            throw new ImageUploaderException(e.getMessage());
        }
    }

    @Override
    public List<FileDataDto> allFiles(User user) {
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listReq);

        List<String> allKeys = listResponse.contents()
                .stream()
                .map(S3Object::key)
                .toList();

        return fileMetadataRepository.findByKeyInAndUploadedBy(allKeys, user)
                .stream()
                .map(fileMetadata -> FileDataDto.builder()
                        .key(fileMetadata.getKey())
                        .contentType(fileMetadata.getContentType())
                        .originalFilename(fileMetadata.getOriginalFilename())
                        .size(fileMetadata.getSize())
                        .uploadedAt(fileMetadata.getUploadedAt())
                        .imageUrl(preSignedUrl(fileMetadata.getKey()))
                        .build()
                )
                .toList();

    }

    @Override
    public String preSignedUrl(String filename) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner
                .presignGetObject(presignRequest)
                .url()
                .toString();
    }

    @Override
    public String getImageUrlByName(String filename) {
        return preSignedUrl(filename);
    }

    @Override
    public void deleteImageByKey(String key, User user) {
        HeadObjectResponse headObjectResponse = s3Client.headObject(HeadObjectRequest.builder()
                .key(key)
                .bucket(bucketName)
                .build());

        Long fileSize = headObjectResponse.contentLength();

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .key(key)
                .bucket(bucketName)
                .build());

        long usedStorage = user.getUsedStorage() - fileSize;
        user.setUsedStorage(Math.max(usedStorage, 0));
        userRepository.save(user);
    }
}