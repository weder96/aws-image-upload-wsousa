package com.wsousa.awsimageupload.repository;

import com.wsousa.awsimageupload.controller.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<Profile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new Profile(UUID.fromString("b3b5d4cc-0773-11ec-9a03-0242ac130003"),
                        "Test Send Image 01",
                        null));
        USER_PROFILES.add(new Profile(UUID.fromString("4faf0987-5810-4df6-a124-3551a2c279e9"),
                        "Test Send Image 02",
                        null));
        USER_PROFILES.add(new Profile(UUID.fromString("57aefdde-043c-4fa0-be3c-1149137f6092"),
                "Test Send Image 03",
                null));
    }

    public List<Profile> getUserProfiles() {
        return USER_PROFILES;
    }
}
