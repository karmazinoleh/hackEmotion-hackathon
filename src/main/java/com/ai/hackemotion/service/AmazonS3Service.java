package com.ai.hackemotion.service;

import java.io.IOException;
import java.io.InputStream;

public interface AmazonS3Service {
    String uploadFile(String fileName, InputStream fileInputStream, String contentType) throws IOException;
}
