package com.petshop.petopia.service;

import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseService {

    @Value("${firebase.storage.image-folder}")
    private String imageFolder;

    public String upload(MultipartFile file) throws IOException {
        String fileName = imageFolder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        StorageClient.getInstance().bucket()
                .create(fileName, file.getBytes(), file.getContentType());

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                StorageClient.getInstance().bucket().getName(),
                fileName.replace("/", "%2F")
        );
    }
}
