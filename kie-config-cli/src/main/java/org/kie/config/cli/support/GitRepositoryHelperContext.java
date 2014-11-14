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
