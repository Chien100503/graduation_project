package com.petshop.petopia.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseService {

    @Value("${firebase.service-account.file-path}")
    private String firebaseConfigPath;

    @Value("${firebase.storage.image-folder}")
    private String imageFolder;

    @PostConstruct
    public void initFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("zenchat-25cfd.appspot.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = imageFolder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        StorageClient.getInstance().bucket()
                .create(fileName, file.getBytes(), file.getContentType());

        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                StorageClient.getInstance().bucket().getName(), fileName.replace("/", "%2F"));
    }
}
