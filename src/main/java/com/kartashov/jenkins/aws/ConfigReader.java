package com.kartashov.jenkins.aws;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

public class ConfigReader {

    private String accessKey;
    private String secretKey;
    private String region;

    public ConfigReader(String credentialsFile, String awsProfile) {
        HierarchicalINIConfiguration configuration = null;
        try {
            configuration = new HierarchicalINIConfiguration(credentialsFile);
            SubnodeConfiguration subnode = configuration.getSection(awsProfile);
            accessKey = subnode.getString("aws_access_key_id");
            secretKey = subnode.getString("aws_secret_access_key");
            region = subnode.getString("region");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRegion() {
        return region;
    }
}
