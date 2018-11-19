package org.kie.wb.test.rest.functional;

import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.Space;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;

public class TroubleshootRestSetupIssueIntegrationTest extends RestTestBase {

    @Test
    @Ignore("This only serves to illustrate the issue, that sometimes deletion of space takes longer." +
            "Please delete this test class when https://issues.jboss.org/browse/AF-1310 is fixed")
    public void troubleshootSetupIssues() {
        for (int i = 1; i < 50; i++) {
            for (int j = 0; j < i; j++) {
                final String
                        spaceName = "space" + j,
                        groupId = "cz.janhrcek";

                final Space space = new Space();
                space.setDefaultGroupId(groupId);
                space.setName(spaceName);
                space.setOwner("jan@rokycan.cz");
                client.createSpace(space);

                final CreateProjectRequest createProjectRequest = new CreateProjectRequest();
                createProjectRequest.setGroupId(groupId);
                createProjectRequest.setVersion("1." + j);
                createProjectRequest.setName("project" + j);

                client.createProject(spaceName, createProjectRequest);
            }
            deleteAllSpaces();
        }
    }
}
