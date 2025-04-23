package com.ai.hackemotion.controller;

import com.ai.hackemotion.service.AmazonS3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final AmazonS3Service amazonS3Service;

    public FileUploadController(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = amazonS3Service.uploadFile(
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getContentType()
            );

            // Повертаємо URL файлу у відповіді
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "File upload failed: " + e.getMessage()));
        }
    }
}
