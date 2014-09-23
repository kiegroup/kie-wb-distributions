package org.kie.smoke.wb.rest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.smoke.wb.AbstractWorkbenchIntegrationTest;
import org.kie.smoke.wb.category.KieDroolsWbSmoke;
import org.kie.smoke.wb.category.KieWbSmoke;

@Category({KieWbSmoke.class, KieDroolsWbSmoke.class})
public class WorkbenchRestSmokeIntegrationTest extends AbstractWorkbenchIntegrationTest {

    @Test
    public void testCreateAndDeleteOrganizationalUnit() {
        System.out.println("KIE integration test!");
    }
}
