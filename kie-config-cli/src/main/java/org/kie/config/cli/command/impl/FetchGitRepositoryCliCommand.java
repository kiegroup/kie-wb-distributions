package org.kie.config.cli.command.impl;

import java.net.URI;

import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.literal.NamedLiteral;
import org.kie.config.cli.CliContext;
import org.kie.config.cli.command.CliCommand;
import org.uberfire.io.IOService;

public class FetchGitRepositoryCliCommand implements CliCommand {

    @Override
    public String getName() {
        return "fetch-git-repo";
    }

    @Override
    public String execute(CliContext context) {

        WeldContainer container = context.getContainer();

        IOService ioService = container.instance().select(IOService.class, new NamedLiteral("configIO")).get();

        String upstream = context.getParameter("git-upstream");
        String gitlocal = context.getParameter("git-local");

        if (upstream == null || gitlocal == null) {
            return "No upstream ("+upstream+") or no local ("+gitlocal+") git repository info available";
        }

        ioService.getFileSystem(URI.create(gitlocal+"?sync="+upstream+"&force=true"));
        return "Fetched successfully";
    }
}
