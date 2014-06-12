package com.kartashov.jenkins.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

public class CloudService {

    private AWSCredentials credentials;
    private Region region;

    public CloudService(Configuration.Profile profile) {
        credentials = new BasicAWSCredentials(profile.getAccessKey(), profile.getSecretKey());
        region = Region.getRegion(Regions.fromName(profile.getRegion()));
    }
}
