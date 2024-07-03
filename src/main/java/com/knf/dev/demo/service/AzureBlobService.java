package com.knf.dev.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;

@Service
public class AzureBlobService {

    @Autowired
    private BlobContainerClient blobContainerClient;

    public String uploadToAzure(MultipartFile multipartFile) throws IOException {
        BlobClient blobClient = blobContainerClient.getBlobClient(multipartFile.getOriginalFilename());
        blobClient.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);
        return multipartFile.getOriginalFilename();
    }

    public byte[] getFileFromAzure(String fileName) throws URISyntaxException {
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.download(outputStream);
        return outputStream.toByteArray();
    }

    public List<String> listAzureBlobs() {
        PagedIterable<BlobItem> blobs = blobContainerClient.listBlobs();
        List<String> blobNames = new ArrayList<>();
        for (BlobItem blobItem : blobs) {
            blobNames.add(blobItem.getName());
        }
        return blobNames;
    }

    public void deleteAzureBlob(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.delete();
    }
}
