package com.ai.hackemotion.controller;

import com.ai.hackemotion.dto.request.UserAssetEmotionRequest;
import com.ai.hackemotion.service.AssetService;
import com.ai.hackemotion.service.impl.AmazonS3ServiceImpl;
import com.ai.hackemotion.service.impl.AssetServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final AmazonS3ServiceImpl amazonS3ServiceImpl;
    private final AssetServiceImpl assetServiceImpl;

    public FileController(AmazonS3ServiceImpl amazonS3ServiceImpl, AssetServiceImpl assetServiceImpl) {
        this.amazonS3ServiceImpl = amazonS3ServiceImpl;
        this.assetServiceImpl = assetServiceImpl;
    }

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
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) throws IOException {
        byte[] fileData = amazonS3ServiceImpl.downloadFile(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(fileData);
    }

    @GetMapping("/download/{userName}")
    public ResponseEntity<byte[]> downloadFiles(@PathVariable String userName) throws IOException{

        List<UserAssetEmotionRequest> assets = assetServiceImpl.getAssetsByUsername(userName);
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
