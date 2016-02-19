package org.kie.server.controller.api.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.controller.api.model.ContainerSpecData;
import org.kie.server.controller.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.MergeMode;
import org.kie.server.controller.api.model.ReleaseId;
import org.kie.server.controller.api.model.RuntimeStrategy;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.ScannerStatus;
import org.kie.server.controller.api.model.spec.ServerConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.model.spec.impl.ContainerSpecImpl;
import org.kie.server.controller.api.model.spec.impl.ProcessConfigImpl;
import org.kie.server.controller.api.model.spec.impl.RuleConfigImpl;
import org.kie.server.controller.api.model.spec.impl.ServerTemplateImpl;
import org.kie.server.controller.api.model.spec.impl.ServerTemplateKeyImpl;
import org.kie.server.controller.api.service.RuleCapabilitiesService;
import org.kie.server.controller.api.service.RuntimeManagementService;
import org.kie.server.controller.api.service.SpecManagementService;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class TestBackend implements SpecManagementService,
                                    RuntimeManagementService,
                                    RuleCapabilitiesService {

    ContainerSpec containerSpec1 = new ContainerSpecImpl( "ct1",
                                                          "My Container 1",
                                                          new ServerTemplateKeyImpl( "id1", null ),
                                                          new ReleaseId( "org.kie:test:LATEST" ),
                                                          KieContainerStatus.STARTED,
                                                          new HashMap<Capability, ContainerConfig>() {{
                                                              put( Capability.RULE, new RuleConfigImpl( 1000L, ScannerStatus.DISPOSED ) );
                                                              put( Capability.PROCESS, new ProcessConfigImpl( RuntimeStrategy.PER_REQUEST, "my kbase", "default", MergeMode.OVERRIDE_ALL ) );
                                                          }} );

    ServerTemplate serverTemplate1 = new ServerTemplateImpl( "id1",
                                                             "MyTemplate",
                                                             new ArrayList<Capability>() {{
                                                                 add( Capability.RULE );
                                                             }},
                                                             Collections.<Capability, ServerConfig>emptyMap(),
                                                             new ArrayList<ContainerSpec>() {{
                                                                 add( containerSpec1 );
                                                             }} );

    ServerTemplate serverTemplate2 = new ServerTemplateImpl( "id2", "MyTemplate 2" );

    @Override
    public void saveContainerSpec( final String serverTemplateId,
                                   final ContainerSpec containerSpec ) {

    }

    @Override
    public void saveServerTemplate( final ServerTemplate serverTemplate ) {

    }

    @Override
    public ServerTemplate getServerTemplate( final String serverTemplateId ) {
        if ( serverTemplateId.equals( "id1" ) ) {
            return serverTemplate1;
        }
        return serverTemplate2;
    }

    @Override
    public Collection<ServerTemplateKey> listServerTemplateKeys() {
        Collection<ServerTemplateKey> temp = new ArrayList<ServerTemplateKey>();

        temp.add( serverTemplate1 );
        temp.add( serverTemplate2 );

        return temp;
    }

    @Override
    public Collection<ServerTemplate> listServerTemplates() {
        return null;
    }

    @Override
    public Collection<ContainerSpec> listContainerSpec( final String serverTemplateId ) {
        return null;
    }

    @Override
    public void deleteContainerSpec( final ContainerSpecKey containerSpecKey ) {

    }

    @Override
    public void deleteServerTemplate( final String serverTemplateId ) {

    }

    @Override
    public void copyServerTemplate( final String serverTemplateId,
                                    final String newServerTemplateId,
                                    final String newServerTemplateName ) {

    }

    private int updateContainer = 0;

    @Override
    public ContainerConfig updateContainerConfig( final ContainerSpecKey containerSpecKey,
                                                  final ContainerConfig containerConfig ) {
        checkNotNull( "containerSpecKey", containerSpecKey );
        checkNotNull( "containerConfig", containerConfig );
        updateContainer++;
        if ( updateContainer % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
        if ( containerSpec1.getId().equals( containerSpecKey.getId() ) &&
                containerSpec1.getServerTemplateKey().getId().equals( containerSpecKey.getServerTemplateKey().getId() ) ) {
            if ( containerConfig instanceof ProcessConfig ) {
                containerSpec1.getConfigs().put( Capability.PROCESS, containerConfig );
                return containerConfig;
            }
        }
        return null;
    }

    @Override
    public ServerConfig updateServerTemplateConfig( final ServerTemplateKey serverTemplateKey,
                                                    final ServerConfig serverTemplateConfig ) {
        return null;
    }

    private int startContainer = 0;

    @Override
    public void startContainer( final ContainerSpecKey containerSpecKey ) {
        startContainer++;
        if ( startContainer % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }

        if ( containerSpec1.getId().equals( containerSpecKey.getId() ) &&
                containerSpec1.getServerTemplateKey().getId().equals( containerSpecKey.getServerTemplateKey().getId() ) ) {

            containerSpec1 = new ContainerSpecImpl( "ct1",
                                                    "My Container 1",
                                                    new ServerTemplateKeyImpl( "id1", null ),
                                                    new ReleaseId( "org.kie:test:LATEST" ),
                                                    KieContainerStatus.STARTED,
                                                    new HashMap<Capability, ContainerConfig>() {{
                                                        put( Capability.RULE, containerSpec1.getConfigs().get( Capability.RULE ) );
                                                        put( Capability.PROCESS, containerSpec1.getConfigs().get( Capability.PROCESS ) );
                                                    }} );

            serverTemplate1 = new ServerTemplateImpl( "id1",
                                                      "MyTemplate",
                                                      new ArrayList<Capability>() {{
                                                          add( Capability.RULE );
                                                      }},
                                                      Collections.<Capability, ServerConfig>emptyMap(),
                                                      new ArrayList<ContainerSpec>() {{
                                                          add( containerSpec1 );
                                                      }} );
        }
    }

    private int stopContainer = 0;

    @Override
    public void stopContainer( final ContainerSpecKey containerSpecKey ) {
        stopContainer++;
        if ( stopContainer % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }

        if ( containerSpec1.getId().equals( containerSpecKey.getId() ) &&
                containerSpec1.getServerTemplateKey().getId().equals( containerSpecKey.getServerTemplateKey().getId() ) ) {

            containerSpec1 = new ContainerSpecImpl( "ct1",
                                                    "My Container 1",
                                                    new ServerTemplateKeyImpl( "id1", null ),
                                                    new ReleaseId( "org.kie:test:LATEST" ),
                                                    KieContainerStatus.STOPPED,
                                                    new HashMap<Capability, ContainerConfig>() {{
                                                        put( Capability.RULE, containerSpec1.getConfigs().get( Capability.RULE ) );
                                                        put( Capability.PROCESS, containerSpec1.getConfigs().get( Capability.PROCESS ) );
                                                    }} );

            serverTemplate1 = new ServerTemplateImpl( "id1",
                                                      "MyTemplate",
                                                      new ArrayList<Capability>() {{
                                                          add( Capability.RULE );
                                                      }},
                                                      Collections.<Capability, ServerConfig>emptyMap(),
                                                      new ArrayList<ContainerSpec>() {{
                                                          add( containerSpec1 );
                                                      }} );

        }

    }

    private int scanNow = 0;

    @Override
    public void scanNow( final ContainerSpecKey containerSpecKey ) {
        scanNow++;
        if ( scanNow % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
    }

    private int startScanner = 0;

    @Override
    public void startScanner( final ContainerSpecKey containerSpecKey,
                              final int interval ) {
        startScanner++;
        if ( startScanner % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
    }

    private int stopScanner = 0;

    @Override
    public void stopScanner( final ContainerSpecKey containerSpecKey ) {
        stopScanner++;
        if ( stopScanner % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
    }

    private int upgrade = 0;

    @Override
    public void versionUpgrade( final ContainerSpecKey containerSpecKey,
                                final String version ) {
        upgrade++;
        if ( upgrade % 3 == 0 ) {
            throw new RuntimeException( "ERROR!" );
        }
    }

    @Override
    public Collection<ServerTemplateKey> getServerInstanceKey( final String serverTemplateId ) {
        return Collections.emptyList();
    }

    @Override
    public Collection<ServerInstance> getServerInstances( final String serverTemplateId ) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Container> getContainers( final String serverInstanceId ) {
        return Collections.emptyList();
    }

    @Override
    public ContainerSpecData getContainers( final ContainerSpecKey containerSpecKey ) {
        return new ContainerSpecData( (ContainerSpec) containerSpecKey,
                                      Collections.<Container>emptyList() );

    }
}
