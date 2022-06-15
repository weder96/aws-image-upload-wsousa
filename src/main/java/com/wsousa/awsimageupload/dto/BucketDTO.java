package com.wsousa.awsimageupload.dto;


public class BucketDTO {
    private String name;

    public BucketDTO() {
    }

    public BucketDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
