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
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class PluginRecorder extends Recorder {

    private String credentialsFilePath;
    private String profileName;

    @DataBoundConstructor
    public PluginRecorder(String credentialsFilePath, String profileName) {
        super();
        this.credentialsFilePath = credentialsFilePath;
        this.profileName = profileName;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        Configuration.Profile profile;
        try {
            profile = new Configuration(credentialsFilePath).getProfile(profileName);
        } catch (Configuration.Exception e) {
            listener.getLogger().println("[ERROR] " + e.getMessage());
            return false;
        }
        CloudService service = new CloudService(profile);

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

        public FormValidation doCheckCredentialsFilePath(@QueryParameter String credentialsFilePath) {
            try {
                new Configuration(credentialsFilePath);
                return FormValidation.ok();
            } catch (Configuration.Exception e) {
                return FormValidation.error(e.getMessage());
            }
        }

        public FormValidation doCheckProfileName(@QueryParameter String credentialsFilePath,
                                                 @QueryParameter String profileName) {
            try {
                new Configuration(credentialsFilePath).getProfile(profileName);
                return FormValidation.ok();
            } catch (Configuration.Exception e) {
                return FormValidation.error(e.getMessage());
            }
        }
    }

    public String getCredentialsFilePath() {
        return credentialsFilePath;
    }

    public String getProfileName() {
        return profileName;
    }
}
