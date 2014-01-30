package org.kie.config.cli.support;

import java.io.InputStreamReader;
import java.util.Scanner;

import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class InteractiveUsernamePasswordCredentialsProvider extends UsernamePasswordCredentialsProvider {

    private InputReader reader;

    public InteractiveUsernamePasswordCredentialsProvider(String username, String password, InputReader reader) {
        super(username, password);
        this.reader = reader;
    }

    @Override
    public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
        try {
            return super.get(uri, items);
        } catch (UnsupportedCredentialItem e) {
            for (CredentialItem i : items) {
                if (i instanceof CredentialItem.YesNoType) {
                    System.out.println(i.getPromptText() + " [yes|no]:");
                    String response = reader.nextLine();
                    if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")) {
                        ((CredentialItem.YesNoType) i).setValue(true);
                    } else {
                        ((CredentialItem.YesNoType) i).setValue(false);
                    }
                    return true;
                } else {
                    continue;
                }
            }
        }
        return false;
    }

}
