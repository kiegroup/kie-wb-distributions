package org.kie.integration.tomcat;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.catalina.Realm;
import org.apache.catalina.realm.GenericPrincipal;

public class TomcatRealmLoginModule implements LoginModule {

    private CallbackHandler handler;
    private Subject subject;

    protected boolean committed = false;
    protected Principal principal = null;
    protected Principal rolePrincipal = null;

    private static Realm applicationRealm;

    public static void setRealm(Realm realm) {
        applicationRealm = realm;
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {

        this.handler = callbackHandler;
        this.subject = subject;
    }

    @Override
    public boolean login() throws LoginException {

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("login");
        callbacks[1] = new PasswordCallback("password", true);

        try {
            handler.handle(callbacks);
            String name = ((NameCallback) callbacks[0]).getName();
            String password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());

            principal = applicationRealm.authenticate(name, password);

            if (principal != null) {
                return true;
            }

            // If credentials are NOT OK we throw a LoginException
            throw new LoginException("Authentication failed");

        } catch (IOException e) {
            throw new LoginException(e.getMessage());
        } catch (UnsupportedCallbackException e) {
            throw new LoginException(e.getMessage());
        }

    }

    @Override
    public boolean commit() throws LoginException {

        // If authentication was not successful, just return false
        if (principal == null) {
            return (false);
        }

        // Add our Principal to the Subject if needed
        if (!subject.getPrincipals().contains(principal)) {
            subject.getPrincipals().add(principal);
            // add roles as special Principal that implements java.security.acl.Group
            if (principal instanceof GenericPrincipal) {
                String roles[] = ((GenericPrincipal) principal).getRoles();
                rolePrincipal = new SimpleRolePrincipal(roles);
                subject.getPrincipals().add(rolePrincipal);
            }
        }

        committed = true;
        return (true);

    }

    @Override
    public boolean abort() throws LoginException {
        if(this.principal == null) {
            return false;
        } else {
            if(this.committed) {
                this.logout();
            } else {
                this.committed = false;
                this.principal = null;
                this.rolePrincipal = null;
            }

            return true;
        }
    }

    @Override
    public boolean logout() throws LoginException {
        this.subject.getPrincipals().remove(this.principal);
        this.subject.getPrincipals().remove(this.rolePrincipal);
        this.committed = false;
        this.principal = null;
        this.rolePrincipal = null;
        return true;
    }

}