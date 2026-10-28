package com.novel.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinIoUtils {
    private final MinioClient minioClient;
    @Value("${minio.bucket-name}")
    private String bucketName;
    public String uploadAvatar(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择图片");
        }

        String objectKey = "avatars/" + UUID.randomUUID();

        try (InputStream input = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("novel-bucket")
                            .object(objectKey)
                            .stream(input, file.getSize(), -1)
                            .contentType("application/octet-stream")
                            .build()
            );
        }

        return objectKey;
    }
}
