/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.config.cli;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.paging.PageResponse;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

@ApplicationScoped
public class EnvironmentProvider {

    private final IOService ioService = new IOServiceDotFileImpl();

    public static final Role ADMIN_ROLE = new Role() {
        @Override
        public String getName() {
            return "admin";
        }
    };

    @Inject
    CliIdentity cliIdentity;

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Alternative
    public SessionInfo getSessionInfo() {
        CliIdentity identity = new CliIdentity();
        return new SessionInfoImpl( identity.getIdentifier(), identity );
    }

    @Produces
    public AuthorizationManager getAuthManager() {
        return new RuntimeAuthorizationManager() {
            @Override
            public boolean supports( Resource resource ) {
                if ( resource instanceof RuntimeResource ) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean authorize( Resource resource,
                                      User subject ) throws UnauthorizedException {
                return subject.getRoles().contains( ADMIN_ROLE );
            }
        };
    }

    @Produces
    public RefactoringQueryService getRefactoringQueryService() {
        return new RefactoringQueryService() {
            @Override
            public Set<String> getQueries() {
                return Collections.emptySet();
            }

            @Override
            public Set<IndexTerm> getTerms( String queryName ) {
                return Collections.emptySet();
            }

            @Override
            public PageResponse<RefactoringPageRow> query( RefactoringPageRequest request ) {
                return new PageResponse<RefactoringPageRow>();
            }

            @Override
            public List<RefactoringPageRow> query( String queryName,
                                                   Set<ValueIndexTerm> queryTerms,
                                                   boolean useWildcards ) {
                return Collections.emptyList();
            }
        };
    }

    @Produces
    public AuthenticationService getAuthenticationService() {
        return new AuthenticationService() {
            @Override
            public User login( String s,
                               String s2 ) {
                return cliIdentity;
            }

            @Override
            public boolean isLoggedIn() {
                return false;
            }

            @Override
            public void logout() {
            }

            @Override
            public User getUser() {
                return cliIdentity;
            }
        };
    }

}
