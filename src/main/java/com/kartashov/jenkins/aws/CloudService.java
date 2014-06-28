package com.kartashov.jenkins.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;

public class CloudService {

    private LoggerFacade logger;

    public CloudService(AWSCredentials credentials, Region region, LoggerFacade logger) {
        this.logger = logger;
    }
}
