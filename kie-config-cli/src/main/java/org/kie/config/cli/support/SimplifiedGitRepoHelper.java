package org.kie.config.cli.support;

import org.guvnor.structure.backend.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.config.SecureConfigItem;
import org.guvnor.structure.server.repositories.RepositoryFactoryHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.guvnor.structure.repositories.impl.git.GitRepository.SCHEME;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class SimplifiedGitRepoHelper implements RepositoryFactoryHelper {

    @Inject
    private PasswordService secureService;

    @Override
    public boolean accept(final ConfigGroup repoConfig) {
        checkNotNull("repoConfig", repoConfig);
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem(EnvironmentParameters.SCHEME);
        checkNotNull("schemeConfigItem", schemeConfigItem);
        return SCHEME.equals(schemeConfigItem.getValue());
    }

    @Override
    public Repository newRepository(final ConfigGroup repoConfig) {
        checkNotNull("repoConfig", repoConfig);
        final ConfigItem<String> schemeConfigItem = repoConfig.getConfigItem(EnvironmentParameters.SCHEME);
        checkNotNull("schemeConfigItem", schemeConfigItem);

        String branch = repoConfig.getConfigItemValue(EnvironmentParameters.BRANCH);
        if (branch == null) {
            branch = "master";
        }

        final GitRepository repo = new GitRepository(repoConfig.getName());
//        repo.changeBranch( branch );

        for (final ConfigItem item : repoConfig.getItems()) {
            if (item instanceof SecureConfigItem) {
                repo.addEnvironmentParameter(item.getName(), secureService.decrypt(item.getValue().toString()));
            } else {
                repo.addEnvironmentParameter(item.getName(), item.getValue());
            }
        }

        if (!repo.isValid()) {
            throw new IllegalStateException("Repository " + repoConfig.getName() + " not valid");
        }

        return repo;
    }

}