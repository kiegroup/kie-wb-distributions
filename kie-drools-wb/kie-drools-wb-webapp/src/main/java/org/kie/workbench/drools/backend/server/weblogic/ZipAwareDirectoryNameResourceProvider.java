package org.kie.workbench.drools.backend.server.weblogic;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.solder.logging.Logger;
import org.jboss.solder.servlet.resource.WebResourceLocationProvider;

public class ZipAwareDirectoryNameResourceProvider  implements WebResourceLocationProvider {

    private final Logger log = Logger.getLogger(ZipAwareDirectoryNameResourceProvider.class);

    @Override
    public int getPrecedence() {
        return 75;
    }

    @Override
    public URL getWebResource(String path, ClassLoader classLoader) {

        // try to load a resource that MUST exist
        String relativeResourceName = this.getClass().getName().replace('.', '/') + ".class";
        URL knownResource = classLoader.getResource(relativeResourceName);

        // we cannot proceed without that resource
        if (knownResource == null) {
            if (log.isDebugEnabled()) {
                log.debug("Could not find resource: " + relativeResourceName);
            }
            return null;
        }

        // we will now work on the absolute path of this URL
        String url = knownResource.toString();

        if (log.isTraceEnabled()) {
            log.trace("Found known resource: " + url);
        }

        // is the resource located inside a JAR file? Remove the jar-specific part.
        if (url.startsWith("jar:") && url.contains("!")) {

            url = url.substring(4, url.lastIndexOf("!"));

            if (log.isTraceEnabled()) {
                log.trace("Location of JAR file containing the resource: " + url);
            }

        }

        // is the resource located inside a zip file? Remove the zip-specific part.
        if (url.startsWith("zip:") && url.contains("!")) {

            url = url.substring(4, url.lastIndexOf("!"));

            if (log.isTraceEnabled()) {
                log.trace("Location of ZIP file containing the resource: " + url);
            }

        }

        // should always work as the URL is built using an existing URL
        try {

            // try to locate the WEB-INF directory
            int i = url.lastIndexOf("/WEB-INF/");
            if (i >= 0) {
                return new URL(url.substring(0, i) + path);
            }

        } catch (MalformedURLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to create URL instance!", e);
            }
        }
        return null;

    }
}
