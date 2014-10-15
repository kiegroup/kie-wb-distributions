package org.kie.workbench.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.kie.services.api.IdentityProvider;

@SessionScoped
public class UberFireIdentityProvider implements IdentityProvider, Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private User identity;
    @Inject
    @RequestScoped
    private Instance<HttpServletRequest> request;

    @Override
    public String getName() {
        try {

            return identity.getIdentifier();
        } catch (Exception e) {
            if (!request.isUnsatisfied() && request.get().getUserPrincipal() != null) {
                return request.get().getUserPrincipal().getName();
            }
            return null;
        }
    }

    @Override
    public List<String> getRoles() {
        List<String> roles = new ArrayList<String>();

        final Set<Role> ufRoles = identity.getRoles();
        for (Role role : ufRoles) {
            roles.add(role.getName());
        }

        return roles;
    }

    @Override
    public boolean hasRole(String role) {
        if (!request.isUnsatisfied()) {
            return request.get().isUserInRole(role);
        }
        return false;
    }

}