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

package org.kie.smoke.wb.util.unit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.conn.HttpHostConnectException;
import org.junit.Assume;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * This is a rule to ignore a test if a GET on the given URL does not succeed.
 * </p>
 * For example:
 * <pre>
 *   &#064Rule
 *   public GetIgnoreRule liveServerRule = new GetIgnoreRule();
 * 
 *   &#064Test
 *   &#064IgnoreIfGetFails(getUrl="http://localhost:8080/kie-wb/rest/deployment")
 *   public myTestDependsOnANonTestServer() { 
 *   // etc..
 * </pre>
 */
public class GetIgnoreRule implements MethodRule {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface IgnoreIfGetFails {
        String getUrl() default "";
    }

    @Override
    public Statement apply( Statement base, FrameworkMethod method, Object target ) {
        Statement result = base;
        if( hasConditionalIgnoreAnnotation(method) ) {
            String urlString = getGetUrl(target, method);
            String message = "Ignored because [GET] " + urlString + " failed.";
            boolean liveServer = false;
            try {
                new URL(urlString); // check that url is a valid url string
                liveServer = true;
            } catch( MalformedURLException e ) {
                liveServer = false;
                message = "Ignored because [" + urlString + "] is not a valid URL.";
            }
            if( liveServer ) {
                try {
                    Response response = Request.Get(urlString).execute();
                    int code = response.returnResponse().getStatusLine().getStatusCode();
                    if( code > 401 ) {
                        liveServer = false;
                        message = "Ignored because [GET] " + urlString + " returned " + code;
                    }
                } catch( HttpHostConnectException hhce ) {
                    liveServer = false;
                    message = "Ignored because server is not available: " + hhce.getMessage(); 
                } catch( Exception e ) {
                    liveServer = false;
                    message = "Ignored because [GET] " + urlString + " threw: " + e.getMessage();
                }
            }
            if( !liveServer ) {
                result = new IgnoreStatement(message);
            }
        }
        return result;
    }

    private boolean hasConditionalIgnoreAnnotation( FrameworkMethod method ) {
        return method.getAnnotation(IgnoreIfGetFails.class) != null;
    }

    private String getGetUrl( Object instance, FrameworkMethod method ) {
        IgnoreIfGetFails annotation = method.getAnnotation(IgnoreIfGetFails.class);
        return annotation.getUrl();
    }

    private static class IgnoreStatement extends Statement {
        private final String message;

        IgnoreStatement(String host) {
            this.message = host;
        }

        @Override
        public void evaluate() {
            Assume.assumeTrue(message, false);
        }
    }

}