/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.backend;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutHelper {

    public static final Logger logger = LoggerFactory.getLogger(LogoutHelper.class);

    public static final void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            request.logout();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            // JBPM-5378: In the latest EAP 7.x versions SSO does not works if the cookie is not removed
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/dashbuilder");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        catch ( SecurityException e ) {
            // The only case we know that this happens is when java security manager is enabled on EAP
            logger.debug( "Security exception happened, without consequences, during logout.", e );
        }
    }
}
