package org.kie.integration.eap.maven.patch;

import org.codehaus.plexus.component.annotations.Component;
import org.kie.integration.eap.maven.eap.EAPContainer;
import org.kie.integration.eap.maven.model.module.EAPModule;
import org.kie.integration.eap.maven.util.EAPArtifactsHolder;

import java.util.Collection;
import java.util.LinkedList;

@Component( role = EAPPatchManager.class )
public class EAPPatchManager {
    
    private EAPPatch[] patches;
    private Collection<EAPStaticModulesPatch> staticPatches;
    private Collection<EAPDynamicModulesPatch> dynamicPatches;
    private EAPContainer container;
    
    public EAPPatchManager() {
        patches = new EAPPatch[] {};
        staticPatches = new LinkedList<EAPStaticModulesPatch>();
        dynamicPatches = new LinkedList<EAPDynamicModulesPatch>();
    }

    public void init(EAPContainer  container, String outputPath, Collection<? extends EAPModule> modules, EAPArtifactsHolder artifactsHolder) {
        this.container = container;
        for (EAPPatch patch : patches) {
            if (patch.doApply(container)) {
                patch.setOutputPath(outputPath);
                patch.setModules(modules);
                patch.setArtifactsHolder(artifactsHolder);

                try {
                    EAPStaticModulesPatch _patch = (EAPStaticModulesPatch) patch;
                    staticPatches.add(_patch);
                } catch (ClassCastException e) {
                    // It's not a static one.
                }

                try {
                    EAPDynamicModulesPatch _patch = (EAPDynamicModulesPatch) patch;
                    dynamicPatches.add(_patch);
                } catch (ClassCastException e) {
                    // It's not a dynamic one.
                }
            }
        }
    }

    public void executeAll() throws EAPPatchException {
        for (EAPPatch patch : patches) {
            execute(patch);
        }
    }

    public void executeDynamicModulePatches() throws EAPPatchException {
        for (EAPPatch patch : dynamicPatches) {
            execute(patch);
        }
    }
    
    public void iterateDynamic(EAPPatchRunnable runnable) throws EAPPatchException {
        for (EAPPatch patch : dynamicPatches) {
            runnable.execute(patch);
        }
    }

    public void iterateStatic(EAPPatchRunnable runnable) throws EAPPatchException {
        for (EAPPatch patch : staticPatches) {
            runnable.execute(patch);
        }
    }

    public void executeStaticModulePatches() throws EAPPatchException {
        for (EAPPatch patch : staticPatches) {
            execute(patch);
        }
    }
    
    protected void execute(EAPPatch patch) throws EAPPatchException {
        if (patch.doApply(container)) patch.execute();
    }

    public EAPPatch[] getPatches() {
        return patches;
    }

    public Collection<EAPStaticModulesPatch> getStaticModulePatches() {
        return staticPatches;
    }
    
    public Collection<EAPDynamicModulesPatch> getDynamicModulePatches() {
        return dynamicPatches;
    }

    public void setPatches(EAPPatch[] patches) {
        this.patches = patches;
    }
    
    public static interface EAPPatchRunnable {
        void execute(EAPPatch patch) throws EAPPatchException;
    }
}
