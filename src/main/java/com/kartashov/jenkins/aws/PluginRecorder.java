package com.kartashov.jenkins.aws;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;

public class PluginRecorder extends Recorder {

    private String credentialsFile;
    private String profile;

    @DataBoundConstructor
    public PluginRecorder(String credentialsFile, String profile) {
        super();
        this.credentialsFile = credentialsFile;
        this.profile = profile;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        ConfigReader reader = new ConfigReader(credentialsFile, profile);
        CloudService service = new CloudService(reader);

        listener.getLogger().println("Plugin executed");

        return true;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            super(PluginRecorder.class);
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Publish to AWS";
        }

        @Override
        public PluginRecorder newInstance(StaplerRequest request, JSONObject formData) throws FormException {
            return request.bindJSON(PluginRecorder.class, formData);
        }

        public FormValidation doCheckCredentialsFile(@QueryParameter String credentialsFile) {
            File f = new File(credentialsFile);
            if (!f.exists()) {
                return FormValidation.error("The specified file does not exist");
            } else if (f.isDirectory()) {
                return FormValidation.error("The specified path points to a directory");
            } else {
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckProfile(@QueryParameter String credentialsFile,
                                             @QueryParameter String profile) {
            if (profile.isEmpty()) {
                return FormValidation.error("Profile cannot be empty");
            }
            File file = new File(credentialsFile);
            try {
                if (!FileUtils.readFileToString(file).contains("[" + profile + "]")) {
                    return FormValidation.error("Cannot find the section [" + profile + "]");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return FormValidation.error("Cannot read credentials file");
            }
            if (doCheckCredentialsFile(credentialsFile).kind.equals(FormValidation.Kind.OK)) {
                ConfigReader reader = new ConfigReader(credentialsFile, profile);
                if (reader.getAccessKey().isEmpty()) {
                    return FormValidation.error("Access key is missing");
                }
                if (reader.getSecretKey().isEmpty()) {
                    return FormValidation.error("Secret key is missing");
                }
                if (reader.getRegion().isEmpty()) {
                    return FormValidation.error("Region is missing");
                }
                return FormValidation.ok();
            } else {
                // return ok for now as we cannot check the aws profile yet
                return FormValidation.ok();
            }
        }
    }

    public String getCredentialsFile() {
        return credentialsFile;
    }

    public String getProfile() {
        return profile;
    }
}
