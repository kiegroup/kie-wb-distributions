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

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;

public class ListRepositoriesCliCommand extends AbstractCliCommand {

	@Override
	public String getName() {
		return "list-repo";
	}

	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		RepositoryService repositoryService = container.instance().select(RepositoryService.class).get();
		Collection<Repository> repositories = repositoryService.getRepositories();

		result.append("Currently available repositories: \n");
		for (Repository config : repositories) {
			result.append("\tRepository " + config.getAlias() + "\n");
			result.append("\t scheme: " + config.getScheme() + "\n");
			result.append("\t uri: " + config.getUri() + "\n");
			result.append("\t environment: " + printEnvironment(config.getEnvironment()) + "\n");
			result.append("\t groups: " + config.getGroups() + "\n");
		}
		return result.toString();
	}

}
