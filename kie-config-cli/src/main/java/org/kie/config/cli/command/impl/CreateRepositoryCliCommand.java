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
package org.kie.config.cli.command.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class CreateRepositoryCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "create-repo";
    }

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        RepositoryService repositoryService = container.instance().select( RepositoryService.class ).get();
        String alias = null;

        InputReader input = context.getInput();
        while (alias == null) {
            System.out.print(">>Repository alias:");
            alias = input.nextLine();

            try {
                new URI("default://localhost/" + alias);
            } catch (URISyntaxException e) {
                System.err.print(">> Invalid value for repository alias: '" + alias + "'");
                alias = null;
            }

        }

        Repository repoCheck = repositoryService.getRepository(alias);
        if (repoCheck != null) {
            result.append(" Repository with alias: '" + alias + "' already exists, cannot proceed");
            return result.toString();
        }

        System.out.print( ">>User:" );
        String user = input.nextLine();

        System.out.print( ">>Password:" );
        String password = context.getInput().nextLineNoEcho();

        System.out.print( ">>Remote origin:" );
        String origin = input.nextLine();

        Map<String, Object> env = new HashMap<String, Object>();
        env.put( "username", user );
        env.put( "crypt:password", password );

        if ( origin.trim().length() > 0 ) {
            env.put( "origin", origin );
        }

        env.put( EnvironmentParameters.MANAGED, false );

        //Mark this Repository as being created by the kie-config-cli tool. This has no affect on the operation
        //of the Repository in the workbench, but it does indicate to kie-config-cli that the Repository should
        //not have its origin overridden when cloning. A local clone is required to manipulate Projects.
        env.put( "org.kie.config.cli.command.CliCommand",
                 "CreateRepositoryCliCommand" );

        Repository repo = repositoryService.createRepository( "git", alias, env );
        result.append( "Repository with alias " + repo.getAlias() + " has been successfully created" );

        return result.toString();
    }

}
