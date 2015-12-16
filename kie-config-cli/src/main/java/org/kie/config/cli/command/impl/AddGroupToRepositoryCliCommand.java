/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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


import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class AddGroupToRepositoryCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "add-group-repo";
	}

	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		RepositoryService repositoryService = container.instance().select(RepositoryService.class).get();

        InputReader input = context.getInput();
		System.out.print(">>Repository alias:");
		String alias = input.nextLine();
		
		Repository repo = repositoryService.getRepository(alias);
		if (repo == null) {
			return "No repository " + alias + " was found";
		}
		System.out.print(">>Security groups (comma separated list):");
		String groupsIn = input.nextLine();
		if (groupsIn.trim().length() > 0) {
			
			String[] groups = groupsIn.split(",");
			for (String group : groups) {
				if (repo.getGroups().contains(group)) {
					continue;
				}
				repositoryService.addGroup(repo, group);
				result.append("Group " + group + " added successfully to repository " + repo.getAlias() + "\n");
			}
		}
		
		return result.toString();
	}

}
