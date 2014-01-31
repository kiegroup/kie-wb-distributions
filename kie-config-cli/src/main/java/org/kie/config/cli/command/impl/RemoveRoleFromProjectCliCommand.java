package org.kie.config.cli.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

public class RemoveRoleFromProjectCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "remove-role-repo";
    }

    @Override
    public String execute( CliContext context ) {
        StringBuffer result = new StringBuffer();
        WeldContainer container = context.getContainer();

        OrganizationalUnitService organizationalUnitService = container.instance().select( OrganizationalUnitService.class ).get();
        RepositoryService repositoryService = container.instance().select( RepositoryService.class ).get();
        ExplorerService projectExplorerService = container.instance().select( ExplorerService.class ).get();
        ProjectService projectService = container.instance().select( ProjectService.class ).get();

        InputReader input = context.getInput();
        System.out.print( ">>Repository alias:" );
        String alias = input.nextLine();

        Repository repo = repositoryService.getRepository( alias );
        if ( repo == null ) {
            return "No repository " + alias + " was found";
        }
        OrganizationalUnit ou = null;
        Collection<OrganizationalUnit> units = organizationalUnitService.getOrganizationalUnits();
        for (OrganizationalUnit unit : units) {
            if (unit.getRepositories().contains(repo)) {
                ou = unit;
                break;
            }
        }
        ArrayList<Project> projects = new ArrayList<Project>();
        ProjectExplorerContent content = projectExplorerService.getContent(ou, repo, null, null, null, Collections.EMPTY_SET);
        projects.addAll( content.getProjects() );
        if ( projects.size() == 0 ) {
            return "No projects found in repository " + alias;
        }

        int projectIndex = 0;
        while ( projectIndex == 0 ) {
            System.out.println( ">>Select project:" );
            for ( int i = 0; i < projects.size(); i++ ) {
                System.out.println( ( i + 1 ) + ") " + projects.get( i ).getProjectName() );
            }
            try {
                projectIndex = Integer.parseInt( input.nextLine() );
            } catch ( NumberFormatException e ) {
                System.out.println( "Invalid index" );
            }
            if ( projectIndex < 1 || projectIndex > projects.size() ) {
                projectIndex = 0;
                System.out.println( "Invalid index" );
            }
        }
        Project project = projects.get( projectIndex - 1 );
        if (project.getRoles() == null || project.getRoles().isEmpty()) {
            return "No roles defined for project " + project.getProjectName();
        }

        System.out.print( ">>Security roles (comma separated list):" );
        String rolesIn = input.nextLine();
        if ( rolesIn.trim().length() > 0 ) {
            String[] roles = rolesIn.split( "," );
            for ( String role : roles ) {
                projectService.removeRole( project, role );
                result.append( "Role " + role + " removed successfully from project " + project.getProjectName() + "\n" );
            }
        }

        return result.toString();
    }

}
