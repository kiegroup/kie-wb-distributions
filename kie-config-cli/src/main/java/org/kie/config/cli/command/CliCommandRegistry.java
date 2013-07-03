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
package org.kie.config.cli.command;

import java.util.HashMap;
import java.util.Map;

import org.kie.config.cli.command.impl.AddDeploymentConfigCliCommand;
import org.kie.config.cli.command.impl.AddRepositoryToGroupCliCommand;
import org.kie.config.cli.command.impl.CreateGroupCliCommand;
import org.kie.config.cli.command.impl.CreateRepositoryCliCommand;
import org.kie.config.cli.command.impl.ExitCliCommand;
import org.kie.config.cli.command.impl.HelpCliCommand;
import org.kie.config.cli.command.impl.ListDeploymentsCliCommand;
import org.kie.config.cli.command.impl.ListGroupsCliCommand;
import org.kie.config.cli.command.impl.ListRepositoriesCliCommand;
import org.kie.config.cli.command.impl.RemoveDeploymentConfigCliCommand;
import org.kie.config.cli.command.impl.RemoveGroupCliCommand;
import org.kie.config.cli.command.impl.RemoveRepositoryCliCommand;
import org.kie.config.cli.command.impl.RemoveRepositoryFromGroupCliCommand;

public class CliCommandRegistry {

	private static CliCommandRegistry instance;
	
	private Map<String, CliCommand> commands = new HashMap<String, CliCommand>();
	
	private CliCommandRegistry() {
		commands.put("exit", new ExitCliCommand());
        commands.put("help", new HelpCliCommand());
        commands.put("list-deployment", new ListDeploymentsCliCommand());
        commands.put("list-repo", new ListRepositoriesCliCommand());
        commands.put("list-group", new ListGroupsCliCommand());
        commands.put("create-group", new CreateGroupCliCommand());
        commands.put("remove-group", new RemoveGroupCliCommand());
        commands.put("add-deployment", new AddDeploymentConfigCliCommand());
        commands.put("remove-deployment", new RemoveDeploymentConfigCliCommand());
        commands.put("create-repo", new CreateRepositoryCliCommand());
        commands.put("remove-repo", new RemoveRepositoryCliCommand());
        commands.put("add-repo-group", new AddRepositoryToGroupCliCommand());
        commands.put("remove-repo-group", new RemoveRepositoryFromGroupCliCommand());
	}
	
	public static CliCommandRegistry get() {
		if (instance == null) {
			instance = new CliCommandRegistry();
		}
		
		return instance;
	}
	
	public CliCommand getCommand(String name) {
		
		return commands.get(name);
	}
}
