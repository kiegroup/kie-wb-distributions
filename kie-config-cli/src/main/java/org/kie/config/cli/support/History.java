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

package org.kie.config.cli.support;

import java.util.ArrayList;
import java.util.List;

public class History {

    private static List<String> history = new ArrayList<String>();
    private static int current = 0;

    public static void addToHistory(String value) {
        history.add(value);
        current = history.size();
    }

    public static String getPrevious() {
        current--;
        String value = getFromHistory();
        if (value == null) {
            current = 0;
        }

        return value;
    }

    public static String getNext() {
        current++;
        String value = getFromHistory();

        if (value == null) {
            current = history.size();
        }
        return value;
    }

    protected static String getFromHistory() {
        if (history.isEmpty()) {
            return null;
        }

        try {

            return history.get(current);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
