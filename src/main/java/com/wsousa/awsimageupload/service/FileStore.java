package com.wsousa.awsimageupload.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.wsousa.awsimageupload.domain.BucketName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {

    private final AmazonS3 s3;

    @Autowired
    public FileStore(AmazonS3 s3) {
        this.s3 = s3;
    }

    public void save(String path,
                     String fileName,
                     InputStream inputStream,
                     Optional<Map<String, String>> optionalMetadata) {
        ObjectMetadata metadata = new ObjectMetadata();
        optionalMetadata.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(metadata::addUserMetadata);
            }
        });

        try {
            s3.putObject(path, fileName, inputStream, metadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to store file to s3", e);
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = s3.getObject(path, key);
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download image from s3", e);
        }
    }


    public List<S3ObjectSummary> listObjects() {
        final String USAGE = "\n" +
                "To run this example, supply the name of a bucket to list!\n" +
                "\n" +
                "Ex: ListObjects <bucket-name>\n";

        String bucket_name = BucketName.PROFILE_IMAGE.getBucketName();

        System.out.format("Objects in S3 bucket %s:\n", bucket_name);
        ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        return objects;
    }


    public List<Bucket> listBuckets() {
        List<Bucket> buckets = s3.listBuckets();
        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }
        return buckets;
    }

    public List<S3ObjectSummary> listObjectsByBuckets(String bucket) {
        System.out.format("Objects in S3 bucket %s:\n", bucket);
        ListObjectsV2Result result = s3.listObjectsV2(bucket);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        return objects;
    }

    public Bucket getBucket(String bucket_name) {
        Bucket bucketFind = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                bucketFind = b;
            }
        }
        return bucketFind;
    }

    public Bucket createBucketS3(String bucket_name) {
        Bucket b = null;
        if (s3.doesBucketExistV2(bucket_name)) {
            System.out.format("Bucket %s already exists.\n", bucket_name);
            b = getBucket(bucket_name);
        } else {
            try {
                b = s3.createBucket(bucket_name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
        return b;
    }

    public Bucket createBucket(String name) {
        System.out.format("\nCreating S3 bucket: %s\n", name);
        Bucket b = createBucketS3(name);
        if (b == null) {
            System.out.println("Error creating bucket!\n");
        }
        return b;
    }
}
