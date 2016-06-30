/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.wb.test.rest;

public enum User {

    REST_ALL("restAll", "restAll1234;", true),
    REST_PROJECT("restProject", "restProject1234;", true),
    NO_REST("noRest", "noRest1234;", false);

    private final String userName;
    private final String password;
    private final boolean authorized;

    User(String userName, String password, boolean authorized) {
        this.userName = userName;
        this.password = password;
        this.authorized = authorized;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAuthorized() {
        return authorized;
    }
}
