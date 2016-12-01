/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.wb.selenium.ui;

import org.junit.Test;
import org.kie.wb.selenium.model.KieSeleniumTest;
import org.kie.wb.selenium.model.persps.ArtifactRepositoryPerspective;
import org.kie.wb.selenium.model.persps.HomePerspective;
import org.kie.wb.selenium.model.persps.ProjectAuthoringPerspective;
import org.kie.wb.selenium.model.persps.ProjectLibraryPerspective;
import org.kie.wb.selenium.model.persps.authoring.ProjectEditor;
import org.kie.wb.selenium.util.Repository;
import org.kie.wb.selenium.util.Waits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.*;

public class ProjectAuthoringIntegrationTest extends KieSeleniumTest {

    private static final Logger LOG = LoggerFactory.getLogger( ProjectAuthoringIntegrationTest.class );

    @Test
    public void importAndBuildProject() {
        HomePerspective home = login.loginDefaultUser();
        ProjectAuthoringPerspective authoring = home.getNavbar().projectAuthoring();

        //ProjectAuthoring will direct to ProjectLibrary if there are no Projects in the workbench
        if ( !authoring.isDisplayed() ) {
            LOG.info( "ProjectAuthoringPerspective not displayed. Trying fallback ProjectLibraryPerspective.." );
            ProjectLibraryPerspective library = home.getNavbar().projectLibrary();
            library.importExampleProject( Repository.JBPM_PLAYGROUND, "MyRepo", "MyOrgUnit", "Evaluation" );
        } else {
            authoring.importExampleProject( Repository.JBPM_PLAYGROUND, "MyRepo", "MyOrgUnit", "Evaluation" );
        }

        authoring = home.getNavbar().projectAuthoring();
        ProjectEditor pe = authoring.openProjectEditor();
        pe.buildAndDeploy();
        Waits.pause( 10_000 ); //Wait for project to build/deploy and appear in artifact repository perspective

        ArtifactRepositoryPerspective artifactRepo = authoring.getNavbar().artifactRepository();
        assertThat( artifactRepo.isArtifactPresent( "org.jbpm:Evaluation:1.0" ) )
                .as( "project artifact should be present after build & deploy" ).isTrue();
    }
}