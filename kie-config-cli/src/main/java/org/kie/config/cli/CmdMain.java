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
package org.kie.config.cli;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import org.kie.config.cli.command.CliCommand;
import org.kie.config.cli.command.CliCommandRegistry;
import org.kie.config.cli.command.impl.CloneGitRepositoryCliCommand;

public class CmdMain {

    public static void main(String[] args) {
        // most important settings driven by jvm system properties
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("java.awt.headless", "true");
        // use scanner to read user input
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        // ask for niogit parent folder so it will operate on the right system repo
        System.out.println("********************************************************\n");
        System.out.println("************* Welcome to Kie config CLI ****************\n");
        System.out.println("********************************************************\n");
        CliContext context = null;

        if (args != null && args.length > 0 && "offline".equalsIgnoreCase(args[0])) {
            // use kie-config-cli in offline mode - server that owns the .niogit is down
            //  to avoid conflicts on concurrent updates
            System.out.println(">>Please specify location of the parent folder of .niogit");

            String niogitPath = scanner.nextLine();
            exitIfRequested(niogitPath);


            boolean foundFolder = false;
            while (!foundFolder) {
                File niogitParent = new File(niogitPath);
                if (!niogitParent.exists() || !niogitParent.isDirectory() || !isNiogitDir(niogitParent)){

                    System.out.println(".niogit folder not found: Try again[1] or continue to create new one[2]?:");
                    String answer = scanner.nextLine();
                    if ("2".equalsIgnoreCase(answer)) {
                        System.setProperty("org.uberfire.nio.git.dir", niogitPath);
                        foundFolder = true;
                    } else {
                        System.out.println(">>Please specify location of the parent folder of .niogit");
                        niogitPath = scanner.nextLine();
                        exitIfRequested(niogitPath);
                    }
                }else {
                    System.setProperty("org.uberfire.nio.git.dir", niogitPath);
                    foundFolder = true;
                }
            }
            context = CliContext.buildContext(scanner);
        } else {
            // use temp folder for clone of repository
            String niogitPath = System.getProperty("java.io.tmpdir") + File.separator + "kie-tmp-repo";
            new File(niogitPath).mkdir();
            System.setProperty("org.uberfire.nio.git.dir", niogitPath);
            context = CliContext.buildContext(scanner);
            context.addParameter("tmp-dir", niogitPath);
            CliCommand command = new CloneGitRepositoryCliCommand();
            command.execute(context);
        }

        System.out.println(">>Please enter command (type help to see available commands): ");
        
        while(scanner.hasNext()) {
	        String commandName = scanner.nextLine();
	        
	        CliCommand command = CliCommandRegistry.get().getCommand(commandName);
	        if (command != null) {
	        	try {
	        	Object result = command.execute(context);
		        	System.out.println("Result:");
		        	System.out.println(result);
		        	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
	        	} catch (Throwable e) {
	        		System.err.println("Unhandled exception caught while executing command " + commandName + " error: " + e.getMessage());
	        		e.printStackTrace();
	        	}
	        	System.out.println(">>Please enter command (type help to see available commands): ");
	        } else {
	        	List<String> matches = CliCommandRegistry.get().findMatching(commandName);
	        	if (matches.isEmpty()) {
	        		System.out.println("No command found for '" + commandName + "'");
	        	} else {
	        		System.out.println("Command '" + commandName + "' not found, did you mean:");
	        		for (String cmd : matches) {
	        			System.out.println("\t" + cmd);
	        		}
	        	}
	        }
        }

    }
    
    private static void exitIfRequested(String input) {
    	// allow to quit
        if ("exit".equalsIgnoreCase(input)) {
        	System.exit(0);
        }
		
	}

	private static boolean isNiogitDir(File parentFolder) {
    	
    	String[] matchingFiles = parentFolder.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if (name.equals(".niogit")) {
					return true;
				}
				return false;
			}
		});
    	
    	if (matchingFiles.length == 1) {
    		return true;
    	}
    	
    	return false;
    }
}
