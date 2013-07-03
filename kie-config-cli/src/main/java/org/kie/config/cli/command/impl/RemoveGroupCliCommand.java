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

import java.util.Collection;
import java.util.Scanner;

import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationService;

public class RemoveGroupCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "remove-group";
	}

	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		GroupService groupService = container.instance().select(GroupService.class).get();
		ConfigurationService configService = container.instance().select(ConfigurationService.class).get();
		
		Scanner input = context.getInput();
		System.out.print(">>Group name:");
		String name = input.nextLine();
		
		Group group = groupService.getGroup(name);
		if (group == null) {
			return "No group " + name + " was found, exiting";
		}
		// TODO add removeGroup to GroupService to make sure cache is refreshed
		Collection<ConfigGroup> groups = configService.getConfiguration(ConfigType.GROUP);
		for (ConfigGroup cgroup : groups) {
			
			if (cgroup.getName().equals(name)) {
			
				boolean removed = configService.removeConfiguration(cgroup);
				result.append("Group " + name + " was removed successfully("+removed+")");
				break;
			}
		
		}
		return result.toString();
	}

}
