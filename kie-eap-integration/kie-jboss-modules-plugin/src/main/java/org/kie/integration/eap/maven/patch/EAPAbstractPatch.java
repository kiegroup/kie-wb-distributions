package org.kie.integration.eap.maven.patch;

import org.kie.integration.eap.maven.model.module.EAPModule;
import org.kie.integration.eap.maven.util.EAPArtifactsHolder;

import java.util.Collection;

public abstract class EAPAbstractPatch implements EAPPatch {
    
    private String outputPath;
    private Collection<? extends EAPModule> modules;
    private EAPArtifactsHolder artifactsHolder;

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public Collection<? extends EAPModule> getModules() {
        return modules;
    }

    public void setModules(Collection<? extends EAPModule> modules) {
        this.modules = modules;
    }

    public EAPArtifactsHolder getArtifactsHolder() {
        return artifactsHolder;
    }

    public void setArtifactsHolder(EAPArtifactsHolder artifactsHolder) {
        this.artifactsHolder = artifactsHolder;
    }
}
