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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.security.Resource;
import org.uberfire.security.Role;
import org.uberfire.security.Subject;
import org.uberfire.security.authz.AuthorizationException;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.security.server.cdi.SecurityFactory;

import static org.uberfire.backend.server.repositories.SystemRepository.*;

@ApplicationScoped
public class EnvironmentProvider {

    private final IOService ioService = new IOServiceDotFileImpl();

    public static final Role ADMIN_ROLE = new Role() {
        @Override
        public String getName() {
            return "admin";
        }
    };

    @PostConstruct
    public void setup() {
        //Use dummy AuthorizationManager that approves everything
        SecurityFactory.setAuthzManager( new AuthorizationManager() {
            @Override
            public boolean supports( Resource resource ) {
                if ( resource instanceof RuntimeResource ) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean authorize( Resource resource,
                                      Subject subject ) throws AuthorizationException {
                return subject.getRoles().contains( ADMIN_ROLE );
            }
        } );
    }

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return SYSTEM_REPO;
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

}
