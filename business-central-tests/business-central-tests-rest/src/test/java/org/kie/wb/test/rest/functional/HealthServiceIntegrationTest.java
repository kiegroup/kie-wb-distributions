package org.kie.wb.test.rest.functional;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.RestWorkbenchClient;
import org.kie.wb.test.rest.client.WorkbenchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthServiceIntegrationTest extends RestTestBase {

    private static Logger log = LoggerFactory.getLogger(HealthServiceIntegrationTest.class);
    private static final String SUCCESS = "success";

    private static WorkbenchClient asyncClient;

    @BeforeClass
    public static void setUp() {
        asyncClient = RestWorkbenchClient.createAsyncWorkbenchClient(URL, USER_ID, PASSWORD);
    }

    @Test
    public void testGetReady() {
        String ready = asyncClient.isReady();

        log.info("Readiness status: {}", ready);

        Assertions.assertThat(ready).contains(SUCCESS).contains(String.valueOf(true));
    }

    @Test
    public void testGetHealthy() {
        String ready = asyncClient.isHealthy();

        log.info("Healthiness status: {}", ready);

        Assertions.assertThat(ready).contains(SUCCESS).contains(String.valueOf(true));
    }
}
