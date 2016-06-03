/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.config.cli.support;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.uberfire.java.nio.file.api.FileSystemProviders;

public class ConfigurationManager {

    private static final String UNIX_EXEC = "sh";
    private static final String UNIX_COMMAND_SETUP = "stty -icanon min 1 -icrnl -inlcr -ixon -echo < /dev/tty";
    private static final String UNIX_COMMAND_RESTORE = "stty sane < /dev/tty";

    public static InputReader configure() {
        // most important settings driven by jvm system properties

        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
        System.setProperty("java.awt.headless", "true");
        System.setProperty("org.uberfire.start.method", "none");
        // switch terminal to proper mode to support history and tabs
        String osname = System.getProperty("os.name").toLowerCase();
        // to be able to use fully featured console, it needs to be unix and true console not within IDE
        if (!osname.contains("windows") && System.console() != null && System.getProperty("org.kie.cli.scanner") == null) {
            try {
                exec(UNIX_EXEC, UNIX_COMMAND_SETUP);
                Runtime.getRuntime().addShutdownHook(new Thread(){
                    @Override
                    public void run() {
                        try {
                            exec(UNIX_EXEC, UNIX_COMMAND_RESTORE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return new InputReader(System.in, false);
            }catch (Exception e) {
                throw new IllegalStateException("Configuration failed due to " + e.getMessage(), e);
            }
        }

        FileSystemProviders.getDefaultProvider();
        // operate on complete lines
        return new InputReader(System.in, true);
    }


    private static String exec(final String executable, final String cmd) throws IOException, InterruptedException {

        return exec(executable, "-c", cmd);
    }

    private static String exec(final String... cmd) throws IOException, InterruptedException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Process p = Runtime.getRuntime().exec(cmd);

        InputStream in = null;
        InputStream err = null;
        OutputStream out = null;
        try {
            int c;
            in = p.getInputStream();
            while ((c = in.read()) != -1) {
                bout.write(c);
            }
            err = p.getErrorStream();
            while ((c = err.read()) != -1) {
                bout.write(c);
            }
            out = p.getOutputStream();
            p.waitFor();
        }
        finally {
            close(in, out, err);
        }

        String result = bout.toString();

        return result;
    }

    private static void close(final Closeable... closeables) {
        for (Closeable c : closeables) {
            try {
                c.close();
            }
            catch (Exception e) {
                // Ignore
            }
        }
    }
}
