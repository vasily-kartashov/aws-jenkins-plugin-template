package com.kartashov.jenkins.aws;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Configuration {

    private File credentialsFile;

    public Configuration(String credentialsFilePath) throws Exception {
        if (credentialsFilePath == null || credentialsFilePath.isEmpty()) {
            throw new Exception("Credentials file is not specified");
        }
        File credentialsFile = new File(credentialsFilePath);
        if (!credentialsFile.exists()) {
            throw new Exception("The specified credentials file does not exist");
        }
        if (credentialsFile.isDirectory()) {
            throw new Exception("The specified path to credentials file is a directory");
        }
        this.credentialsFile = credentialsFile;
    }

    public Profile getProfile(String profileName) throws Exception {
        if (profileName == null || profileName.isEmpty()) {
            throw new Exception("Profile is not specified");
        }
        try {
            if (!FileUtils.readFileToString(credentialsFile).contains("[" + profileName + "]")) {
                throw new Exception("Cannot find the section [" + profileName + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Cannot read credentials file");
        }
        HierarchicalINIConfiguration configuration = null;
        try {
            configuration = new HierarchicalINIConfiguration(credentialsFile);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new Exception("Cannot read configuration file");
        }
        SubnodeConfiguration subnode = configuration.getSection(profileName);
        String accessKey = subnode.getString("aws_access_key_id");
        String secretKey = subnode.getString("aws_secret_access_key");
        String region = subnode.getString("region");
        if (accessKey == null || accessKey.isEmpty()) {
            throw new Exception("Access key is not specified");
        }
        if (secretKey == null || secretKey.isEmpty()) {
            throw new Exception("Secret key is not specified");
        }
        if (region == null || region.isEmpty()) {
            throw new Exception("Region is not specified");
        }
        return new Profile(accessKey, secretKey, region);
    }

    public static class Profile {

        private String accessKey;
        private String secretKey;
        private String region;

        public Profile(String accessKey, String secretKey, String region) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
            this.region = region;
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

    public static class Exception extends java.lang.Exception {
        public Exception(String message) {
            super(message);
        }
    }
}
