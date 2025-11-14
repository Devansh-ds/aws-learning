package com.aws.s3;

import lombok.Builder;

@Builder
public record DocResponse(
        String key,
        String fileUrl
) {
}
