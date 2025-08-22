package com.devansh.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FileDataDto(
        String key,
        String originalFilename,
        String contentType,
        Long size,
        LocalDateTime uploadedAt,
        String imageUrl
) {
}
