package com.ai.hackemotion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
public class AmazonS3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public AmazonS3Service(
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-key}") String secretKey,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.bucket-name}") String bucketName
    ) {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        this.bucketName = bucketName;
    }

    public String uploadFile(String fileName, InputStream fileInputStream, String contentType) throws IOException {
        fileName = fileName.replace(" ", "_");

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                putObjectRequest,
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(fileInputStream, fileInputStream.available()));

        return generateFileUrl(fileName);
    }

    private String generateFileUrl(String fileName) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }
}
