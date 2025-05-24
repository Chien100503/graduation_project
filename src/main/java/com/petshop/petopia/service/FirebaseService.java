package com.petshop.petopia.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseService {

    @Value("${firebase.storage.image-pet}")
    private String imagePet;

    @Value("${firebase.storage.image-product}")
    private String imageProduct;

    @Value("${firebase.storage.image-avatar}")
    private String imageAvatar;

    @Value("${firebase.storage.image-banner}")
    private String imageBanner;

    public String uploadImagePet(MultipartFile file) throws IOException {
        String fileName = imagePet + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        StorageClient.getInstance().bucket()
                .create(fileName, file.getBytes(), file.getContentType());

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                StorageClient.getInstance().bucket().getName(),
                fileName.replace("/", "%2F")
        );
    }

    public String uploadImageProduct(MultipartFile file) throws IOException {
        String fileName = imageProduct + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        StorageClient.getInstance().bucket()
                .create(fileName, file.getBytes(), file.getContentType());

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                StorageClient.getInstance().bucket().getName(),
                fileName.replace("/", "%2F")
        );
    }

    public String uploadImageAvatar(MultipartFile file) throws IOException {
        String fileName = imageAvatar + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        StorageClient.getInstance().bucket()
                .create(fileName, file.getBytes(), file.getContentType());

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                StorageClient.getInstance().bucket().getName(),
                fileName.replace("/", "%2F")
        );
    }

    public String uploadImageBanner(MultipartFile file) throws IOException {
        String fileName = imageBanner + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        StorageClient.getInstance().bucket()
                .create(fileName, file.getBytes(), file.getContentType());

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                StorageClient.getInstance().bucket().getName(),
                fileName.replace("/", "%2F")
        );
    }

    public void deleteFileByUrl(String imageUrl) {
        try {
            String encodedPath = imageUrl.substring(
                    imageUrl.indexOf("/o/") + 3,
                    imageUrl.indexOf("?")
            );

            String filePath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);

            String bucketName = StorageClient.getInstance().bucket().getName();
            Storage storage = StorageClient.getInstance().bucket().getStorage();
            BlobId blobId = BlobId.of(bucketName, filePath);

            storage.delete(blobId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
