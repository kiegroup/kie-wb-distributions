package org.kie.integration.eap.maven.patch;

import org.apache.maven.plugin.MojoFailureException;
import org.kie.integration.eap.maven.distribution.EAPStaticLayerDistribution;
import org.kie.integration.eap.maven.model.graph.EAPModulesGraph;
import org.kie.integration.eap.maven.model.module.EAPDynamicModule;
import org.kie.integration.eap.maven.model.module.EAPModule;
import org.kie.integration.eap.maven.template.EAPTemplateBuilder;
import org.kie.integration.eap.maven.template.assembly.EAPAssemblyTemplate;
import org.kie.integration.eap.maven.util.EAPArtifactsHolder;

import java.util.Collection;

public abstract class EAPDynamicModulesPatch extends EAPAbstractPatch {

    private EAPStaticLayerDistribution staticLayerDistribution;

    public void setStaticLayerDistribution(EAPStaticLayerDistribution staticLayerDistribution) {
        this.staticLayerDistribution = staticLayerDistribution;
    }

    /**
     * 
        Lifecycle methods for dynamic module generation.
        // TODO: Implement pending methods.
     **/

    /**
     * Apply the patch for the module assembly descriptor.
     *
     * @param assemblyTemplate The assembly teamplte model.
     */
    public void patchAssembly(EAPAssemblyTemplate assemblyTemplate) throws EAPPatchException {
        // To be overwritten.
    }
    
}
