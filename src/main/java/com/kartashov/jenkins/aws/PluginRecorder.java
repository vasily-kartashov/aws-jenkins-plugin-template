package com.kartashov.jenkins.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
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
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class PluginRecorder extends Recorder {

    private String profileName;
    private String regionId;
    private String relativeArtifactPath;

    @DataBoundConstructor
    public PluginRecorder(String profileName, String regionId, String relativeArtifactPath) throws IOException {
        super();
        this.profileName = profileName;
        this.regionId = regionId;
        this.relativeArtifactPath = relativeArtifactPath;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        LoggerFacade logger = new LoggerFacade(listener.getLogger());

        logger.box(Messages.build_top_banner());

        if (!build.getWorkspace().child(relativeArtifactPath).exists()) {
            logger.error(Messages.artifact_missing_in_path(build.getWorkspace().child(relativeArtifactPath)));
            return false;
        }

        ConfigurationService configurationService = new ConfigurationService();
        AWSCredentials credentials = null;
        Region region = null;
        try {
            credentials = configurationService.getCredentials(profileName);
            region = configurationService.getRegion(regionId);
        } catch (ConfigurationService.Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        CloudService service = new CloudService(credentials, region, logger);

        // todo add logic

        logger.box(Messages.build_bottom_banner());
        return true;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private ConfigurationService configurationService;

        public DescriptorImpl() {
            super(PluginRecorder.class);
            configurationService = new ConfigurationService();
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.build_step_name();
        }

        @Override
        public PluginRecorder newInstance(StaplerRequest request, JSONObject formData) throws FormException {
            return request.bindJSON(PluginRecorder.class, formData);
        }

        // @todo https://issues.jenkins-ci.org/browse/JENKINS-19124 check this!

        public FormValidation doCheckProfileName(@QueryParameter String profileName) {
            try {
                configurationService.getCredentials(profileName);
            } catch (ConfigurationService.Exception e) {
                return FormValidation.error(e.getMessage());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckRegionId(@QueryParameter String regionId) {
            try {
                configurationService.getRegion(regionId);
            } catch (ConfigurationService.Exception e) {
                return FormValidation.error(e.getMessage());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckRelativeArtifactPath(@QueryParameter String relativeArtifactPath) {
            if (relativeArtifactPath == null || relativeArtifactPath.isEmpty()) {
                return FormValidation.error(Messages.artifact_empty_path());
            }
            if (!relativeArtifactPath.endsWith(".war")) {
                return FormValidation.error(Messages.artifact_wrong_extension(relativeArtifactPath));
            }
            return FormValidation.ok();
        }

        public ListBoxModel doFillRegionIdItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("", "");
            for (Regions r : Regions.values()) {
                items.add(r.getName(), r.getName());
            }
            return items;
        }
    }

    public String getProfileName() {
        return profileName;
    }

    public String getRegionId() {
        return regionId;
    }

    public String getRelativeArtifactPath() {
        return relativeArtifactPath;
    }
}
