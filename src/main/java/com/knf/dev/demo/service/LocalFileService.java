package com.knf.dev.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileService {

    private final String uploadDir = "uploads/";

    public LocalFileService() {
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdir();
        }
    }

    public String uploadToLocal(MultipartFile multipartFile) throws IOException {
        Path filePath = Paths.get(uploadDir, multipartFile.getOriginalFilename());
        Files.copy(multipartFile.getInputStream(), filePath);
        return multipartFile.getOriginalFilename();
    }

    public byte[] getFileFromLocal(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir, fileName);
        return Files.readAllBytes(filePath);
    }

    public List<String> listLocalFiles() {
        List<String> fileNames = new ArrayList<>();
        File folder = new File(uploadDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    public void deleteLocalFile(String fileName) {
        Path filePath = Paths.get(uploadDir, fileName);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
