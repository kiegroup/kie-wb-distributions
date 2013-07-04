package org.kie.config.cli.command.impl;

import java.util.Scanner;

import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;

public class AddRoleToGroupCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "add-role-group";
	}

	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		GroupService groupService = container.instance().select(GroupService.class).get();
		
		Scanner input = context.getInput();
		System.out.print(">>Group name:");
		String name = input.nextLine();
		
		Group group = groupService.getGroup(name);
		if (group == null) {
			return "No group " + name + " was found";
		}
		System.out.print(">>Security roles (comma separated list):");
		String rolesIn = input.nextLine();
		if (rolesIn.trim().length() > 0) {
			
			String[] roles = rolesIn.split(",");
			for (String role : roles) {
				groupService.addRole(group, role);
				result.append("Role " + role + " added successfully to group " + group.getName() + "\n");
			}
		}
		
		return result.toString();
	}

}
