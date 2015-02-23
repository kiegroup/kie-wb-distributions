/*
 * JBoss, Home of Professional Open Source
 * 
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.smoke.wb.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.kie.smoke.wb.util.handler.AbstractResponseHandler;
import org.kie.smoke.wb.util.handler.JsonResponseHandler;
import org.kie.smoke.wb.util.handler.XmlResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("null")
public class RestUtil {

    private static Logger logger = LoggerFactory.getLogger(RestUtil.class);

    // Helper methods -------------------------------------------------------------------------------------------------------------

    private static String basicAuthenticationHeader( String user, String password ) {
        String token = user + ":" + password;
        try {
            return "BASIC " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
        } catch( UnsupportedEncodingException ex ) {
            throw new IllegalStateException("Cannot encode with UTF-8", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> ResponseHandler<T> createResponseHandler( String mediaType, int status, Class... responseTypes ) {
        ResponseHandler<T> rh = null;
        if( MediaType.APPLICATION_XML.equals(mediaType) ) {
            rh = new XmlResponseHandler(status, responseTypes);
        } else if( MediaType.APPLICATION_JSON.equals(mediaType) ) {
            rh = new JsonResponseHandler(status, responseTypes);
        } else {
            fail("Unexpected media type: " + mediaType);
        }
        return rh;
    }

    private static String createBaseUriString( URL deploymentUrl, String relativeUrl ) {
        String uriStr = null;
        try {
            uriStr = deploymentUrl.toURI() + relativeUrl;
        } catch( URISyntaxException urise ) {
            logAndFail("Invalid uri :" + deploymentUrl.toString(), urise);
        }
        return uriStr;
    }

    private static void logOp( String op, String uri ) {
        logger.debug("[" + op + "] " + uri);
    }

    private static void logOp( String op, Object entity, String uri ) {
        logger.debug("[" + op + "] (" + entity.getClass().getSimpleName() + ") " + uri);
    }

    // Public Helper methods ------------------------------------------------------------------------------------------------------

    public static void logAndFail( String msg, Exception e ) {
        logger.error(msg, e);
        fail(msg + ": " + e.getMessage());
    }

    // REST methods -------------------------------------------------------------------------------------------------------------

    public static <T, G> T get( URL deploymentUrl, String relativeUrl, String mediaType, int status, String user, String password,
            Class... responseTypes ) {
        String uriStr = createBaseUriString(deploymentUrl, relativeUrl);

        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);

        // @formatter:off
        Request request = Request.Get(uriStr)
                .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password));
        // @formatter:on

        Response resp = null;
        try {
            logOp("GET", uriStr);
            resp = request.execute();
        } catch( Exception e ) {
            logAndFail("[GET] " + uriStr, e);
        }

        try {
            return resp.handleResponse(rh);
        } catch( Exception e ) {
            logAndFail("Failed retrieving response from [GET] " + uriStr, e);
        }

        // never happens
        return null;
    }

    public static <T, G> T getQuery( URL deploymentUrl, String relativeUrl, String mediaType, int status, String user,
            String password, Map<String, String> queryParams, Class... responseTypes ) {
        URIBuilder uriBuilder = null;
        try {
            String uriStr = createBaseUriString(deploymentUrl, relativeUrl);
            uriBuilder = new URIBuilder(uriStr);
        } catch( URISyntaxException urise ) {
            logAndFail("Invalid uri :" + deploymentUrl.toString(), urise);
        }

        for( Entry<String, String> paramEntry : queryParams.entrySet() ) {
            uriBuilder.addParameter(paramEntry.getKey(), paramEntry.getValue());
        }

        URI uri = null;
        String uriStr = null;
        try {
            uri = uriBuilder.build();
            uriStr = uri.toString();
        } catch( URISyntaxException urise ) {
            logAndFail("Invalid uri!", urise);
        }

        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);

        // @formatter:off
            Request request = Request.Get(uri)
                    .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                    .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password));
            // @formatter:off

            Response resp = null;
            try {
                logOp("GET", uriStr);
                resp = request.execute();
            } catch( Exception e ) {
                logAndFail("[GET] " + uriStr, e);
            }

            try {
                return resp.handleResponse(rh);
            } catch( Exception e ) {
                logAndFail("Failed retrieving response from [GET] " + uriStr, e);
            }

            // never happens
            return null;
        }

    public static <T> T postEntity( URL deploymentUrl, String relativeUrl, 
            int status, String user, String password, 
            Class [] classes,
            Object entity, Class<T>... responseTypes ) {

        String uriStr = createBaseUriString(deploymentUrl, relativeUrl);

        String mediaType = MediaType.APPLICATION_XML;
        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);
        XmlResponseHandler xrh = (XmlResponseHandler) rh;

        xrh.addExtraJaxbClasses(classes);
        String entityStr = xrh.serialize(entity);
        
        HttpEntity bodyEntity = null;
        try {
            bodyEntity = new StringEntity(entityStr);
        } catch( UnsupportedEncodingException uee ) {
            logAndFail("Unable to encode serialized " + entity.getClass().getSimpleName() + " entity", uee);
        }

        // @formatter:off
        Request request = Request.Post(uriStr)
                .body(bodyEntity)
                .addHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password));
        // @formatter:on

        Response resp = null;
        try {
            logOp("POST", entity, uriStr);
            resp = request.execute();
        } catch( Exception e ) {
            logAndFail("[GET] " + uriStr, e);
        }

        try {
            return resp.handleResponse(rh);
        } catch( Exception e ) {
            logAndFail("Failed retrieving response from [GET] " + uriStr, e);
        }

        // never happens
        return null;
    }

    public static <T> T postEntity( URL deploymentUrl, String relativeUrl, String mediaType, int status, String user,
            String password, Object entity, Class<T>... responseTypes ) {
        String uriStr = createBaseUriString(deploymentUrl, relativeUrl);

        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);

        String entityStr = ((AbstractResponseHandler) rh).serialize(entity);
        HttpEntity bodyEntity = null;
        try {
            bodyEntity = new StringEntity(entityStr);
        } catch( UnsupportedEncodingException uee ) {
            logAndFail("Unable to encode serialized " + entity.getClass().getSimpleName() + " entity", uee);
        }

        // @formatter:off
            Request request = Request.Post(uriStr)
                    .body(bodyEntity)
                    .addHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                    .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                    .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password));
            // @formatter:on

        Response resp = null;
        try {
            logOp("POST", entity, uriStr);
            resp = request.execute();
        } catch( Exception e ) {
            logAndFail("[GET] " + uriStr, e);
        }

        try {
            return resp.handleResponse(rh);
        } catch( Exception e ) {
            logAndFail("Failed retrieving response from [GET] " + uriStr, e);
        }

        // never happens
        return null;
    }

    public static <T> T postEntity( URL deploymentUrl, String relativeUrl, String mediaType, int status, String user,
            String password, double timeoutInSecs, Object entity, Class<T>... responseTypes ) {

        String uriStr = createBaseUriString(deploymentUrl, relativeUrl);

        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);

        String entityStr = ((AbstractResponseHandler) rh).serialize(entity);
        HttpEntity bodyEntity = null;
        try {
            bodyEntity = new StringEntity(entityStr);
        } catch( UnsupportedEncodingException uee ) {
            logAndFail("Unable to encode serialized " + entity.getClass().getSimpleName() + " entity", uee);
        }

        // @formatter:off
            Request request = Request.Post(uriStr)
                    .body(bodyEntity)
                    .addHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                    .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                    .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password));
            // @formatter:on

        Response resp = null;
        long before = 0, after = 0;
        try {
            logOp("POST", entity, uriStr);
            before = System.currentTimeMillis();
            resp = request.execute();
            after = System.currentTimeMillis();
        } catch( Exception e ) {
            logAndFail("[GET] " + uriStr, e);
        }

        long duration = after - before;
        assertTrue("Timeout exceeded " + timeoutInSecs + " secs: " + ((double) duration / 1000d) + " secs",
                duration < timeoutInSecs * 1000);

        try {
            return resp.handleResponse(rh);
        } catch( Exception e ) {
            logAndFail("Failed retrieving response from [GET] " + uriStr, e);
        }

        // never happens
        return null;
    }

    public static <T> T post( URL deploymentUrl, String relativeUrl, String mediaType, int status, String user, String password,
            double timeoutInSecs, Class<T>... responseTypes ) {

        String uriStr = createBaseUriString(deploymentUrl, relativeUrl);

        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);

        // @formatter:off
            Request request = Request.Post(uriStr)
                    .addHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                    .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                    .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password));
            // @formatter:on

        Response resp = null;
        long before = 0, after = 0;
        try {
            logOp("POST", uriStr);
            before = System.currentTimeMillis();
            resp = request.execute();
            after = System.currentTimeMillis();
        } catch( Exception e ) {
            logAndFail("[GET] " + uriStr, e);
        }

        long duration = after - before;
        assertTrue("Timeout exceeded " + timeoutInSecs + " secs: " + ((double) duration / 1000d) + " secs",
                duration < timeoutInSecs * 1000);

        try {
            return resp.handleResponse(rh);
        } catch( Exception e ) {
            logAndFail("Failed retrieving response from [GET] " + uriStr, e);
        }

        // never happens
        return null;
    }

    public static <T> T post( URL deploymentUrl, String relativeUrl, String mediaType, int status, String user, String password,
            Class<T>... responseTypes ) {

        String uriStr = createBaseUriString(deploymentUrl, relativeUrl);

        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);

        // @formatter:off
            Request request = Request.Post(uriStr)
                    .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                    .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password));
            // @formatter:on

        Response resp = null;
        try {
            logOp("POST", uriStr);
            resp = request.execute();
        } catch( Exception e ) {
            logAndFail("[GET] " + uriStr, e);
        }

        try {
            return resp.handleResponse(rh);
        } catch( Exception e ) {
            logAndFail("Failed retrieving response from [GET] " + uriStr, e);
        }

        // never happens
        return null;
    }

    public static <T> T postForm( URL deploymentUrl, String relativeUrl, String mediaType, int status, String user,
            String password, Map<String, String> formParams, Class<T>... responseTypes ) {

        String uriStr = createBaseUriString(deploymentUrl, relativeUrl);

        // form content
        Form formContent = Form.form();
        for( Entry<String, String> entry : formParams.entrySet() ) {
            formContent.add(entry.getKey(), entry.getValue());
        }

        // @formatter:off
            Request request = Request.Post(uriStr)
                    .addHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                    .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password))
                .bodyForm(formContent.build());
            // @formatter:on

        Response resp = null;
        try {
            logOp("POST", uriStr);
            resp = request.execute();
        } catch( Exception e ) {
            logAndFail("[GET] " + uriStr, e);
        }

        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);
        try {
            return resp.handleResponse(rh);
        } catch( Exception e ) {
            logAndFail("Failed retrieving response from [GET] " + uriStr, e);
        }

        // never happens
        return null;
    }

    public static <T, G> T delete( URL deploymentUrl, String relativeUrl, String mediaType, int status, String user,
            String password, Class... responseTypes ) {
        String uriStr = createBaseUriString(deploymentUrl, relativeUrl);

        ResponseHandler<T> rh = createResponseHandler(mediaType, status, responseTypes);

        // @formatter:off
            Request request = Request.Delete(uriStr)
                    .addHeader(HttpHeaders.ACCEPT, mediaType.toString())
                    .addHeader(HttpHeaders.AUTHORIZATION, basicAuthenticationHeader(user, password));
            // @formatter:off

            Response resp = null;
            try {
                logOp("DELETE", uriStr);
                resp = request.execute();
            } catch( Exception e ) {
                logAndFail("[GET] " + uriStr, e);
            }

            try {
                return resp.handleResponse(rh);
            } catch( Exception e ) {
                logAndFail("Failed retrieving response from [GET] " + uriStr, e);
            }

            // never happens
            return null;
        }

   
}