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

import static org.uberfire.backend.server.repositories.SystemRepository.SYSTEM_REPO;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

@ApplicationScoped
public class EnvironmentProvider {

	private final IOService ioService = new IOServiceDotFileImpl();
	
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
    
    @Produces
    public Identity getIdentity() {
    	return new Identity() {
			
			private static final long serialVersionUID = -9178650167557721039L;

			@Override
			public String getName() {
				return System.getProperty("user.name");
			}
			
			@Override
			public boolean hasRole(Role role) {
				return false;
			}
			
			@Override
			public List<Role> getRoles() {
				return new ArrayList<Role>();
			}
		};
    }
}
