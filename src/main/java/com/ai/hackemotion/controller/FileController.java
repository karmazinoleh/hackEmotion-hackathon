package com.ai.hackemotion.controller;

import com.ai.hackemotion.dto.request.UserAssetEmotionRequest;
import com.ai.hackemotion.security.service.impl.JwtServiceImpl;
import com.ai.hackemotion.service.impl.AmazonS3ServiceImpl;
import com.ai.hackemotion.service.impl.AssetServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final AmazonS3ServiceImpl amazonS3ServiceImpl;
    private final AssetServiceImpl assetServiceImpl;
    private final JwtServiceImpl jwtServiceImpl;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = amazonS3ServiceImpl.uploadFile(
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

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getFile(HttpServletRequest request, @PathVariable String fileName) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String username = jwtServiceImpl.extractUsername(token);

        if (token == null) { // AOP
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(assetServiceImpl.isAssetAvailableForUser(fileName, username)){
            byte[] fileData = amazonS3ServiceImpl.downloadFile(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(fileData);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFiles(HttpServletRequest request) throws IOException{
        String token = request.getHeader("Authorization");
        String username = jwtServiceImpl.extractUsername(token);

        List<UserAssetEmotionRequest> assets = assetServiceImpl.getAssetsByUsername(username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream);

        StringBuilder labelsCsv = new StringBuilder("image_name,emotion\n");

        for(UserAssetEmotionRequest asset : assets){
            String fileName = asset.getName();
            byte[] fileBytes = amazonS3ServiceImpl.downloadFile(fileName);

            zipOut.putNextEntry(new ZipEntry("images/" + fileName));
            zipOut.write(fileBytes);
            zipOut.closeEntry();

            for(String emotion : asset.getEmotionNames()){
                labelsCsv.append(fileName).append(",").append(emotion).append("\n");
            }
        }

        zipOut.putNextEntry(new ZipEntry("labels.csv"));
        zipOut.write(labelsCsv.toString().getBytes());
        zipOut.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"dataset.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayOutputStream.toByteArray());
    }
}
