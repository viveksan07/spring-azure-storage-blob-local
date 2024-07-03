package com.knf.dev.demo.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.knf.dev.demo.service.AzureBlobService;
import com.knf.dev.demo.service.LocalFileService;

@Controller
@RequestMapping("/files")
public class AzureController {

    @Autowired
    private AzureBlobService azureBlobService;

    @Autowired
    private LocalFileService localFileService;

    @GetMapping
    public String listFiles(Model model) {
        List<FileLocation> files = new ArrayList<>();
        for (String fileName : localFileService.listLocalFiles()) {
            files.add(new FileLocation(fileName, "local"));
        }
        for (String fileName : azureBlobService.listAzureBlobs()) {
            files.add(new FileLocation(fileName, "azure"));
        }
        model.addAttribute("files", files);
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("uploadDestination") String destination, Model model) throws IOException {
        if ("local".equals(destination)) {
            localFileService.uploadToLocal(file);
        } else if ("azure".equals(destination)) {
            azureBlobService.uploadToAzure(file);
        }
        return "redirect:/files";
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName, @RequestParam("location") String location) throws URISyntaxException, IOException {
        ByteArrayResource resource;
        if ("local".equals(location)) {
            resource = new ByteArrayResource(localFileService.getFileFromLocal(fileName));
        } else {
            resource = new ByteArrayResource(azureBlobService.getFileFromAzure(fileName));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(headers).body(resource);
    }

    @GetMapping("/delete")
    public String deleteFile(@RequestParam("fileName") String fileName, @RequestParam("location") String location, Model model) {
        if ("local".equals(location)) {
            localFileService.deleteLocalFile(fileName);
        } else {
            azureBlobService.deleteAzureBlob(fileName);
        }
        return "redirect:/files";
    }
}
