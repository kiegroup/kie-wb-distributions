/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import org.guvnor.structure.deployment.DeploymentConfigService;
import org.jboss.weld.environment.se.WeldContainer;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.support.InputReader;

public class AddDeploymentConfigCliCommand implements CliCommand {

	@Override
	public String getName() {
		return "add-deployment";
	}

	@Override
	public String execute(CliContext context) {
		StringBuffer result = new StringBuffer();
		WeldContainer container = context.getContainer();

		DeploymentConfigService deploymentConfigService = container.instance().select(DeploymentConfigService.class).get();
		
		InputReader input = context.getInput();
		System.out.print(">>GroupId:");
		String groupId = input.nextLine();
		
		System.out.print(">>ArtifactId:");
		String artifactId = input.nextLine();
		
		System.out.print(">>Version:");
		String version = input.nextLine();
		
		System.out.print(">>KBase name:");
		String kbase = input.nextLine();
		
		System.out.print(">>KSession name:");
		String ksession = input.nextLine();
		
		System.out.print(">>Runtime strategy[SINGLETON]:");
		String strategy = input.nextLine();
		if (strategy.trim().length() == 0) {
			strategy = "SINGLETON";
		}
		
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit(groupId, artifactId, version, kbase, ksession, strategy);
		
		deploymentConfigService.addDeployment(unit.getIdentifier(), unit);
		
		result.append("Deployment " + unit.getIdentifier() + " has been successfully added");
		return result.toString();
	}

}
