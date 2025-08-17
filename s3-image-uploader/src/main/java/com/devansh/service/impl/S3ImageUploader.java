package com.devansh.service.impl;

import com.devansh.exception.ImageUploaderException;
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

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3ImageUploader implements ImageUploaderService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${app.s3.bucket}")
    private String bucketName;

    @Override
    public String uploadImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new ImageUploaderException("Image is null or empty");
        }
        try {
            String actualFilename = image.getOriginalFilename();
            String newFilename = UUID.randomUUID().toString() +
                    actualFilename.substring(actualFilename.lastIndexOf("."));

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFilename)
                    .contentType(image.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image.getBytes()));
            return preSignedUrl(newFilename);

        } catch (IOException e) {
            throw new ImageUploaderException("Failed to upload image to S3 bucket" + e);
        }
    }

    @Override
    public List<String> allFiles() {
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listReq);

        return listResponse.contents()
                .stream()
                .map(s3Object -> preSignedUrl(s3Object.key()))
                .collect(Collectors.toList());
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
}


















