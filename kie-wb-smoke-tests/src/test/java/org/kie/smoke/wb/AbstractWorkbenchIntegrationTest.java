package org.kie.smoke.wb;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

abstract public class AbstractWorkbenchIntegrationTest {
    private static Logger logger = LoggerFactory.getLogger(AbstractWorkbenchIntegrationTest.class);

    protected static final URL deploymentUrl;

    static {
        try {
            deploymentUrl = new URL(System.getProperty("deployable.base.uri"));
            logger.info("deployable.base.uri=" + deploymentUrl.toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed deployment URL '" + System.getProperty("deployable.base.uri") + "'!", e);
        }
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
       protected void starting(Description description) {
          System.out.println("Starting test: " + description.getMethodName());
       }
    };

}
