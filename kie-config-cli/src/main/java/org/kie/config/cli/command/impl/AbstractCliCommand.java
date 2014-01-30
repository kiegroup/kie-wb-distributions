package org.kie.config.cli.command.impl;

import java.util.Map;

import org.kie.config.cli.command.CliCommand;

public abstract class AbstractCliCommand implements CliCommand {

    protected String printEnvironment(Map<String, Object> env) {
        StringBuffer data = new StringBuffer();
        data.append("{");
        for (Map.Entry<String, Object> entry : env.entrySet()) {
            data.append(entry.getKey());
            data.append("=");
            if (entry.getKey().equalsIgnoreCase("password")) {
                data.append("****");
            } else {
                data.append(entry.getValue());
            }
            data.append(", ");
        }
        data.delete(data.length()-2, data.length());
        data.append("}");
        return data.toString();
    }
}
