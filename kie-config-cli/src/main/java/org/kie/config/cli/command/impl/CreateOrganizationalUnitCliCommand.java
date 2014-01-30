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

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

public class CreateOrganizationalUnitCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "create-org-unit";
    }

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        OrganizationalUnitService organizationalUnitService = container.instance().select( OrganizationalUnitService.class ).get();
        RepositoryService repositoryService = container.instance().select( RepositoryService.class ).get();

        InputReader input = context.getInput();
        System.out.print( ">>Organizational Unit name:" );
        String name = input.nextLine();

        System.out.print( ">>Organizational Unit owner:" );
        String owner = input.nextLine();

        System.out.print( ">>Repositories (comma separated list):" );
        String repos = input.nextLine();
        Collection<Repository> repositories = new ArrayList<Repository>();
        if ( repos != null && repos.trim().length() > 0 ) {
            String[] repoAliases = repos.split( "," );
            for ( String alias : repoAliases ) {
                Repository repo = repositoryService.getRepository( alias );
                if ( repo != null ) {
                    repositories.add( repo );
                } else {
                    System.out.println( "WARN: Repository with alias " + alias + " does not exists and will be skipped" );
                }
            }
        }

        OrganizationalUnit organizationalUnit = organizationalUnitService.createOrganizationalUnit( name, owner, repositories );
        result.append( "Organizational Unit " + organizationalUnit.getName() + " successfully created" );
        return result.toString();
    }

}
