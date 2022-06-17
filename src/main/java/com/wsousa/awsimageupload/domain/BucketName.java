package com.wsousa.awsimageupload.domain;

public enum BucketName {

    PROFILE_IMAGE("data-lake-crimes");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
