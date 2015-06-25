/*
 * Copyright 2015 JBoss Inc
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

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GitRepositoryHelperContext {

    private String user;
    private String password;
    private String scheme;
    private String host;
    private int port;

    public String getUser() {
        return user;
    }

    public void setUser( final String user ) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme( final String scheme ) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost( final String host ) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort( final int port ) {
        this.port = port;
    }

}
