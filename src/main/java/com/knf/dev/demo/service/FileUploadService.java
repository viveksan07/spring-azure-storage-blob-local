package com.knf.dev.demo.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "upload/";

    public String uploadToLocal(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File destinationFile = new File(UPLOAD_DIR + fileName);
        file.transferTo(destinationFile);
        return fileName;
    }
}
