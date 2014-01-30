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
