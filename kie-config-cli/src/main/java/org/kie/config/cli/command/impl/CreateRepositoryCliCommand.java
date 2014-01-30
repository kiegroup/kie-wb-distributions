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

import java.util.HashMap;
import java.util.Map;

import org.jboss.weld.environment.se.WeldContainer;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

public class CreateRepositoryCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "create-repo";
	}

	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		RepositoryService repositoryService = container.instance().select(RepositoryService.class).get();

        InputReader input = context.getInput();
		System.out.print(">>Repository alias:");
		String alias = input.nextLine();
		
		System.out.print(">>User:");
		String user = input.nextLine();
		
		System.out.print(">>Password:");
		String password = context.getInput().nextLineNoEcho();
		
		System.out.print(">>Remote origin:");
		String origin = input.nextLine();
		
		Map<String, Object> env = new HashMap<String, Object>();
		env.put("username", user);
		env.put("crypt:password", password);

		if (origin.trim().length() > 0) {
			env.put("origin", origin);
		}
		
		Repository repo = repositoryService.createRepository("git", alias, env);
		result.append("Repository with alias " + repo.getAlias() + " has been successfully created");
		
		return result.toString();
	}

}
