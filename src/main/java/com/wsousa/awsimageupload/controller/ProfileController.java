package com.wsousa.awsimageupload.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.wsousa.awsimageupload.dto.BucketDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/profile")
@CrossOrigin("*")
public class ProfileController {

    private final ProfileService userProfileService;

    @Autowired
    public ProfileController(ProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<Profile> getUserProfiles() {
        return userProfileService.getUserProfiles();
    }

    @PostMapping(
            path = "{profileId}/image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void uploadUserProfileImage(@PathVariable("profileId") String userProfileId,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam("bucket") String bucket) {
        userProfileService.uploadUserProfileImage(userProfileId, file, bucket);
    }

    @GetMapping("/bucket/{bucket}/image/{profileId}/download")
    public byte[] downloadProfileImage(@PathVariable("profileId") String userProfileId,
                                       @PathVariable("bucket") String bucket) {
        return userProfileService.downloadProfileImage(userProfileId, bucket);
    }

    @GetMapping("/listObjects")
    public List<S3ObjectSummary> listObjects() {
        return userProfileService.listObjects();
    }

    @GetMapping("/listBuckets")
    public List<Bucket> listBuckets() {
        return userProfileService.listBuckets();
    }

    @Operation(summary = "Get a Bucket by name Bucket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Bucket",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = S3ObjectSummary.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "S3ObjectSummary not found",
                    content = @Content) })
    @GetMapping("/bucket/{bucket}")
    public List<S3ObjectSummary> listObjectsByBuckets(@PathVariable("bucket") String bucket) {
        return userProfileService.listObjectsByBuckets(bucket);
    }

    @PostMapping(value = "/createBucket", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Bucket createBucket(@RequestBody BucketDTO bucketDTO) {
        System.out.println("createBucket :"+ bucketDTO.getName());
        return userProfileService.createBucket(bucketDTO.getName());
    }
}
