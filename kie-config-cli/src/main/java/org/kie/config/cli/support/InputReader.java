/*
 * Copyright 2015 JBoss Inc
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import org.kie.config.cli.command.CliCommandRegistry;

public class InputReader {

    private InputStream input;
    private Scanner scanner;

    public InputReader(InputStream input, boolean useScanner) {
        this.input = input;
        if (useScanner) {
            scanner = new Scanner(new InputStreamReader(this.input));
        }
    }

    public String nextLine() {

        return nextLine(false, true);
    }

    public String nextLineNoEcho() {
        if (scanner != null && System.console() != null) {
            return new String(System.console().readPassword());
        } else {
            return nextLine(false, false);
        }
    }

    public String nextLine(boolean endAtTab, boolean echo) {
        if (scanner != null) {
            return scanner.nextLine();
        }
        StringWriter stringWritter = new StringWriter();
        BufferedWriter writter = new BufferedWriter(stringWritter);
        int data = 0;
        try {
            while ((data = input.read()) != -1) {
                if ( data == 0x1B ) {

                    data = input.read();
                    data = input.read();

                    if (data == 65 ) {
                        //UP ARROW KEY
                        String history = History.getPrevious();

                        if (history != null) {
                            clearLine(stringWritter, writter);
                            // init new writers
                            stringWritter = new StringWriter();
                            writter = new BufferedWriter(stringWritter);
                            System.out.print(history);
                            writter.write(history.toCharArray());
                        }
                    } else if (data == 66 ) {
                        //DOWN ARROW KEY
                        String history = History.getNext();

                        clearLine(stringWritter, writter);
                        // init new writers
                        stringWritter = new StringWriter();
                        writter = new BufferedWriter(stringWritter);
                        if (history != null) {
                            System.out.print(history);
                            writter.write(history.toCharArray());
                        }
                    } else if (data == 67 ) {
                        //RIGH ARROW KEY
                        // do nothing

                    //LEFT ARROW KEY
                    } else if (data == 68 ) {
                        //LEFT ARROW KEY
                        // do ntohing
                    }
                }else if (data == 13) {
                    writter.flush();
                    System.out.println();
                    return stringWritter.toString();
                } else if (endAtTab && data == 9) {
                    writter.flush();
                    String commandName = stringWritter.toString();
                    List<String> matches = CliCommandRegistry.get().findMatching(commandName);
                    if (!matches.isEmpty()) {
                        if (matches.size() == 1) {
                            String matched = matches.get(0);
                            String remaining = matched.replaceFirst(commandName, "");
                            System.out.print(remaining);
                            writter.write(remaining.toCharArray());
                        } else {
                            for (String cmd : matches) {
                                System.out.println("\t" + cmd);
                            }
                            System.out.print(commandName);
                        }
                    }
                    continue;
                } else if (data == 127) {
                    writter.flush();
                    // on delete remove last element
                    StringBuffer buffer = stringWritter.getBuffer();
                    if (buffer.length() > 0) {
                        buffer.deleteCharAt(buffer.length()-1);
                        // close curent writers
                        stringWritter.close();
                        writter.close();
                        // init new writers
                        stringWritter = new StringWriter();
                        writter = new BufferedWriter(stringWritter);
                        // populate remaining data to the writer
                        writter.write(buffer.toString().toCharArray());
                        System.out.print("\b \b");
                    }
                    continue;
                } else {
                    writter.write(data);
                    if (echo) {
                        System.out.print(Character.toChars(data));
                    }
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                stringWritter.close();
                writter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void clearLine(StringWriter stringWritter, BufferedWriter writter) {
        try {
            writter.flush();
            StringBuffer buffer = stringWritter.getBuffer();
            if (buffer.length() > 0) {
                // close curent writers
                stringWritter.close();
                writter.close();
                for (int i = 0; i < buffer.length(); i++) {
                    System.out.print("\b \b");
                }
            }
        } catch (IOException e) {

        }
    }
}
