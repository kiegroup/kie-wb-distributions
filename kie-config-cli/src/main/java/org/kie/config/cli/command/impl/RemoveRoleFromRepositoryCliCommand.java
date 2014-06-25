package org.kie.config.cli.command.impl;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class RemoveRoleFromRepositoryCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "remove-role-repo";
    }

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        RepositoryService repositoryService = container.instance().select( RepositoryService.class ).get();

        InputReader input = context.getInput();
        System.out.print( ">>Repository alias:" );
        String alias = input.nextLine();

        Repository repo = repositoryService.getRepository( alias );
        if ( repo == null ) {
            return "No repository " + alias + " was found";
        }
        if ( repo.getRoles() == null || repo.getRoles().isEmpty() ) {
            return "No roles defined for repository " + alias;
        }
        System.out.print( ">>Security roles (comma separated list):" );
        String rolesIn = input.nextLine();
        if ( rolesIn.trim().length() > 0 ) {

            String[] roles = rolesIn.split( "," );
            for ( String role : roles ) {
                repositoryService.removeRole( repo, role );
                result.append( "Role " + role + " removed successfully from repository " + repo.getAlias() + "\n" );
            }
        }

        return result.toString();
    }

}
