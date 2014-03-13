package org.kie.integration.eap.maven.patch;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.kie.integration.eap.maven.eap.EAPContainer;
import org.kie.integration.eap.maven.template.assembly.EAPAssemblyTemplate;
import org.kie.integration.eap.maven.util.EAPArtifactUtils;

/**
 * This patch impl is concerning webfragment descriptor files (servlet spec 3.0)
 * If a JAR resource contains a <code>webfragment.xml</code> file, and this JAR is placed as a static module resource, this webfragment is not loaded by the JBoss container.
 * So, this patch extracts the <code>webfragment.xml</code> file from JAR artifact and creates a new JAR "on the fly" adding this descriptor. Then, the new generated JAR file is added into the webapp that is used with the current modules distribution.  
 */
public class EAPWebfragmentPatch extends EAPDynamicModulesPatch {
    
    private static final String ID = "dynamic.webfragment";
    private static final ComparableVersion EAP_VERSION = new ComparableVersion("6.1.1");
    private static final ComparableVersion AS_VERSION = new ComparableVersion("7.0");    
    
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean doApply(EAPContainer eap) {
        ComparableVersion version = eap.getVersion();
        EAPContainer.EAPContainerId containerId = eap.getContainerId();
        
        // Apply for EAP >= 6.1.1
        if (containerId.equals(EAPContainer.EAPContainerId.EAP) && 
                (EAPArtifactUtils.isVersionEqualsThan(version, EAP_VERSION) || EAPArtifactUtils.isVersionGreaterThan(version, EAP_VERSION))) {
            return true;
        }

        // Apply for AS >= 7.0
        if (containerId.equals(EAPContainer.EAPContainerId.AS) &&
                (EAPArtifactUtils.isVersionEqualsThan(version, AS_VERSION) || EAPArtifactUtils.isVersionGreaterThan(version, AS_VERSION))) {
            return true;
        }
        
        return false;
    }

    /**
     * Find the module definitions that have this webfragment patch definition and create the target JARs on the fly.
     * @throws EAPPatchException
     */
    @Override
    public void execute() throws EAPPatchException {
        
        // Extract the modules that contain this webfragment patch definition.
        
        // For each one, generate a new JAR on the fly with the wefgragment descriptor.
        
    }

    /**
     * Add the webfragment generated JAR as file inclusion in the module assembly descriptor.
     * 
     * @param assemblyTemplate The assembly teamplte model.
     */
    @Override
    public void patchAssembly(EAPAssemblyTemplate assemblyTemplate) throws EAPPatchException {
        super.patchAssembly(assemblyTemplate);
        
        // Add a fileset - file inclusion for the generated webfragment JAR file.
        
    }
}
