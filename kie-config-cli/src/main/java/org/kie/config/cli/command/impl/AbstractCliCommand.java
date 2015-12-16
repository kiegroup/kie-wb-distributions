/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
