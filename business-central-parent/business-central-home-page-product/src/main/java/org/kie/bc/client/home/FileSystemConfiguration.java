package org.kie.bc.client.home;

public class FileSystemConfiguration {

    private boolean isGitEnabled = false;

    public boolean isGitEnabled() {
        return isGitEnabled;
    }

    public void setGitEnabled(boolean gitEnabled) {
        isGitEnabled = gitEnabled;
    }
}
