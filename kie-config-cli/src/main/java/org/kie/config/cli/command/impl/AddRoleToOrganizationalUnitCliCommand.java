package org.kie.config.cli.command.impl;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class AddRoleToOrganizationalUnitCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "add-role-org-unit";
    }

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        OrganizationalUnitService organizationalUnitService = container.instance().select( OrganizationalUnitService.class ).get();

        InputReader input = context.getInput();
        System.out.print( ">>Organizational Unit name:" );
        String name = input.nextLine();

        OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit( name );
        if ( organizationalUnit == null ) {
            return "No Organizational Unit " + name + " was found";
        }
        System.out.print( ">>Security roles (comma separated list):" );
        String rolesIn = input.nextLine();
        if ( rolesIn.trim().length() > 0 ) {

            String[] roles = rolesIn.split( "," );
            for ( String role : roles ) {
                organizationalUnitService.addRole( organizationalUnit, role );
                result.append( "Role " + role + " added successfully to Organizational Unit " + organizationalUnit.getName() + "\n" );
            }
        }

        return result.toString();
    }

}
