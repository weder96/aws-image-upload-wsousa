package com.wsousa.awsimageupload.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.wsousa.awsimageupload.domain.BucketName;
import com.wsousa.awsimageupload.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class ProfileService {

    private final ProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;

    @Autowired
    public ProfileService(ProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<Profile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

    public void uploadUserProfileImage(String userProfileId, MultipartFile file, String bucket) {
        // 1. Check if image is not empty
        isFileEmpty(file);

        // 2. If file is an image
        isImage(file);

        // 3. The user is exists in our DB
        Profile user = getUserProfile(userProfileId);

        // 4. Grab some metadata from file if any
        Map<String, String> metadata = extractMetadata(file);

        // 5. Store the image in s3 and update DB (userProfileImageLink) with s3 image link
        String path = String.format("%s", bucket);
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
        try {
            fileStore.save(path, fileName, file.getInputStream(), Optional.of(metadata));
            user.setUserProfileImageLink(fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] downloadProfileImage(String key, String bucket) {
        return fileStore.download(bucket, key);
    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private Profile getUserProfile(String userProfileId) {
        return userProfileDataAccessService
                .getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));
    }

    private void isImage(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + " ]");
        }
    }

    public List<S3ObjectSummary> listObjects() {
        return fileStore.listObjects();
    }

    public List<Bucket> listBuckets() {
        return fileStore.listBuckets();
    }

    public List<S3ObjectSummary> listObjectsByBuckets(String bucket) {
        return fileStore.listObjectsByBuckets(bucket);
    }

    public Bucket createBucket(String name) {
        return fileStore.createBucket(name);
    }
}
