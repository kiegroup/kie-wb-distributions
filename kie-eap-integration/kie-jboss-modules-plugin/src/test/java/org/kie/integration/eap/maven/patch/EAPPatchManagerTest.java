package org.kie.integration.eap.maven.patch;

import junit.framework.Assert;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.integration.eap.maven.eap.EAPContainer;
import org.kie.integration.eap.maven.model.module.EAPModule;
import org.kie.integration.eap.maven.util.EAPArtifactsHolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EAPPatchManagerTest {

    private EAPPatchManager tested;
    private EAPStaticModulesPatch staticModulesPatch;
    private EAPDynamicModulesPatch dynamicModulesPatch;
    @Mock
    private EAPContainer container;
    @Mock
    private EAPArtifactsHolder artifactsHolder;
    @Mock
    private Collection<EAPModule> modules;

    @Before
    public void setUp() throws Exception {
        // Init the annotated mocks.
        MockitoAnnotations.initMocks(this);
        
        // Create static and dynamic patch instances.
        staticModulesPatch = new EAPStaticModulesPatch() {
            @Override
            public String getId() {
                return "static";
            }

            @Override
            public boolean doApply(EAPContainer eap) {
                return true;
            }

            @Override
            public void execute() throws EAPPatchException {

            }
        };
        
        dynamicModulesPatch = new EAPDynamicModulesPatch() {
            @Override
            public String getId() {
                return "dynamic";
            }

            @Override
            public boolean doApply(EAPContainer eap) {
                return true;
            }

            @Override
            public void execute() throws EAPPatchException {

            }
        };
        
        Collection<EAPPatch> patches = new LinkedList<EAPPatch>();
        patches.add(staticModulesPatch);
        patches.add(dynamicModulesPatch);
        
        // Create the mocked container.
        when(container.getContainerId()).thenReturn(EAPContainer.EAPContainerId.EAP);
        when(container.getVersion()).thenReturn(new ComparableVersion("6.1.1"));
        
        // Create the tested instance.
        tested = new EAPPatchManager();
        tested.setPatches(patches.toArray(new EAPPatch[patches.size()]));
    }

    @Test
    public void testInit() throws Exception {
        String outputPath = "output-path-1";
        
        tested.init(container, outputPath, modules, artifactsHolder);
        
        assertNotNull(tested.getStaticModulePatches());
        assertTrue(tested.getStaticModulePatches().size() == 1);
        assertNotNull(tested.getDynamicModulePatches());
        assertTrue(tested.getDynamicModulePatches().size() == 1);

        EAPStaticModulesPatch resultStatic = tested.getStaticModulePatches().iterator().next();
        assertNotNull(resultStatic);
        assertEquals(resultStatic.getOutputPath(), outputPath);
        assertTrue(resultStatic.getArtifactsHolder() == artifactsHolder);
        assertTrue(resultStatic.getModules() == modules);

        EAPDynamicModulesPatch resultDynamic = tested.getDynamicModulePatches().iterator().next();
        assertNotNull(resultDynamic);
        assertEquals(resultDynamic.getOutputPath(), outputPath);
        assertTrue(resultDynamic.getArtifactsHolder() == artifactsHolder);
        assertTrue(resultDynamic.getModules() == modules);
    }

    // TODO: @Test
    public void testExecute() throws Exception {
        
    }

    @After
    public void tearDown() throws Exception {

    }
    
}
