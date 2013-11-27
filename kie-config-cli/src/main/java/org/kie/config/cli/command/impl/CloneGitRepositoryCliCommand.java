package org.kie.config.cli.command.impl;

import java.io.Console;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.literal.NamedLiteral;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;

public class CloneGitRepositoryCliCommand implements CliCommand {

    private static final String GIT_LOCAL = "git://system";
    private static final String GIT_DEFAULT_UPSTREAM = "ssh://localhost:8001/system";

    @Override
    public String getName() {
        return "clone-git-repo";
    }

    @Override
    public String execute(CliContext context) {

        WeldContainer container = context.getContainer();

        IOService ioService = container.instance().select(IOService.class, new NamedLiteral("configIO")).get();
        System.out.println(">>Please specify location of remote git system repository [" + GIT_DEFAULT_UPSTREAM + "]");

        String systemGitRepoUrl = context.getInput().nextLine();
        if (systemGitRepoUrl == null || "".equals(systemGitRepoUrl.trim())) {
            systemGitRepoUrl = GIT_DEFAULT_UPSTREAM;
        }
        String password = "";
        System.out.println(">>Please specify password");
        Console console = System.console();
        if (console != null) {
            password = new String(console.readPassword());
        } else {
            password = context.getInput().nextLine();
        }
        if (password == null || "".equals(password.trim())) {
            password = "";
        }

        context.addParameter("git-upstream", systemGitRepoUrl);
        context.addParameter("git-local", GIT_LOCAL);

        Map<String, String> env = new HashMap<String, String>();
        env.put("origin", systemGitRepoUrl);
        env.put("username", System.getProperty("git.user", System.getProperty( "user.name" )));
        env.put("password", System.getProperty("git.password", password));
        env.put("init", "true");
        try {
            ioService.newFileSystem(URI.create(GIT_LOCAL), env);
        } catch (FileSystemAlreadyExistsException e) {
            ioService.getFileSystem(URI.create(GIT_LOCAL+"?push="+systemGitRepoUrl+"&force"));
        }
        return "Cloned successfully";
    }
}
