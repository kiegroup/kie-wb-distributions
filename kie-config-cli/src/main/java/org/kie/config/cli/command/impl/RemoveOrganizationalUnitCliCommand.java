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

import java.util.Scanner;

import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;

public class RemoveOrganizationalUnitCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "remove-org-unit";
    }

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        OrganizationalUnitService organizationalUnitService = container.instance().select( OrganizationalUnitService.class ).get();

        Scanner input = context.getInput();
        System.out.print( ">>Organizational Unit name:" );
        String name = input.nextLine();

        OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit( name );
        if ( organizationalUnit == null ) {
            return "No Organizational Unit " + name + " was found, exiting";
        }
        organizationalUnitService.removeOrganizationalUnit( name );
        result.append( "Organizational Unit " + name + " was removed successfully" );

        return result.toString();
    }

}
