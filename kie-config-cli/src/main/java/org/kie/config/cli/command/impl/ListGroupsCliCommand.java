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

import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;

public class ListGroupsCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "list-group";
	}

	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		GroupService groupService = container.instance().select(GroupService.class).get();
		Collection<Group> groups = groupService.getGroups();

		result.append("Currently available groups: \n");
		for (Group config : groups) {
			result.append("\tGroup " + config.getName() + "\n");
			result.append("\towner: " + config.getOwner() + "\n");
			result.append("\trepositories: \n");
			for (Repository repository : config.getRepositories()) {
				result.append("\t\tRepository " + repository.getAlias() + "\n");
				result.append("\t\t\t scheme: " + repository.getScheme() + "\n");
				result.append("\t\t\t uri: " + repository.getUri() + "\n");
				result.append("\t\t\t environment: " + repository.getEnvironment() + "\n");
			}
		}
		return result.toString();
	}

}
