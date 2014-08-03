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
package org.kie.smoke.tests.util;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestUtil {

    private static Logger logger = LoggerFactory.getLogger(RestUtil.class);

    /**
     * Call the DELETE HTTP method on the given request
     * @param restRequest The {@link ClientRequest} instance with the request
     * @param mediaType The {@link MediaType} that should be used with the request
     * @return A {@link ClientResponse} instance with the response info
     */
    public static ClientResponse<?> delete(ClientRequest restRequest, MediaType mediaType) {
        return httpMethodReturnResponse(DEL, restRequest, mediaType, 200);
    }

    /**
     * Call the GET HTTP method on the given request
     * @param restRequest The {@link ClientRequest} instance with the request
     * @param mediaType The {@link MediaType} that should be used with the request
     * @return A {@link ClientResponse} instance with the response info
     */
    public static ClientResponse<?> get(ClientRequest restRequest, MediaType mediaType) {
        return httpMethodReturnResponse(GET, restRequest, mediaType, 200);
    }

    /**
     * Call the POST HTTP method on the given request
     * @param restRequest The {@link ClientRequest} instance with the request
     * @param mediaType The {@link MediaType} that should be used with the request
     * @return A {@link ClientResponse} instance with the response info
     */
    public static ClientResponse<?> post(ClientRequest restRequest, MediaType mediaType) {
        return httpMethodReturnResponse(POST, restRequest, mediaType, 200);
    }

    /**
     * Call the DELETE HTTP method on the given request and return the actual entity returned
     * @param restRequest The {@link ClientRequest} instance with the request information
     * @param mediaType The {@link MediaType} that should be used with the request
     * @param responseType The {@link Class} of the return type
     * @return The actual entity returned by the request: this can be null
     */
    public static <T> T  delete(ClientRequest restRequest, MediaType mediaType, Class<T> responseType) {
        return httpMethodReturnType(DEL, restRequest, mediaType, 200, responseType);
    }

    /**
     * Call the GET HTTP method on the given request and return the actual entity returned
     * @param restRequest The {@link ClientRequest} instance with the request information
     * @param mediaType The {@link MediaType} that should be used with the request
     * @param responseType The {@link Class} of the return type
     * @return The actual entity returned by the request: this can be null
     */
    public static <T> T get(ClientRequest restRequest, MediaType mediaType, Class<T> responseType) {
        return httpMethodReturnType(GET, restRequest, mediaType, 200, responseType);
    }

    /**
     * Call the POST HTTP method on the given request and return the actual entity returned
     * @param restRequest The {@link ClientRequest} instance with the request information
     * @param mediaType The {@link MediaType} that should be used with the request
     * @param responseType The {@link Class} of the return type
     * @return The actual entity returned by the request: this can be null
     */
    public static <T> T post(ClientRequest restRequest, MediaType mediaType, Class<T> responseType) {
        return httpMethodReturnType(POST, restRequest, mediaType, 200, responseType);
    }
    
    /**
     * Call the DELETE HTTP method on the given request and return the actual entity returned
     * @param restRequest The {@link ClientRequest} instance with the request information
     * @param mediaType The {@link MediaType} that should be used with the request
     * @param responseType The {@link Class} of the return type
     * @param status The HTTP status that the request should return
     * @return The actual entity returned by the request: this can be null
     */
    public static <T> T  delete(ClientRequest restRequest, MediaType mediaType, int status, Class<T> responseType) {
        return httpMethodReturnType(DEL, restRequest, mediaType, status, responseType);
    }

    /**
     * Call the GET HTTP method on the given request and return the actual entity returned
     * @param restRequest The {@link ClientRequest} instance with the request information
     * @param mediaType The {@link MediaType} that should be used with the request
     * @param responseType The {@link Class} of the return type
     * @param status The HTTP status that the request should return
     * @return The actual entity returned by the request: this can be null
     */
    public static <T> T get(ClientRequest restRequest, MediaType mediaType, int status, Class<T> responseType) {
        return httpMethodReturnType(GET, restRequest, mediaType, status, responseType);
    }

    /**
     * Call the POST HTTP method on the given request and return the actual entity returned
     * @param restRequest The {@link ClientRequest} instance with the request information
     * @param mediaType The {@link MediaType} that should be used with the request
     * @param responseType The {@link Class} of the return type
     * @param status The HTTP status that the request should return
     * @return The actual entity returned by the request: this can be null
     */
    public static <T> T post(ClientRequest restRequest, MediaType mediaType, int status, Class<T> responseType) {
        return httpMethodReturnType(POST, restRequest, mediaType, status, responseType);
    }

    private static ClientResponse<?> httpMethodReturnResponse(int type, ClientRequest restRequest, MediaType mediaType, int status) {
        setAcceptHeader(restRequest, mediaType);
        ClientResponse<?> responseObj = logAndExecuteRequest(type, restRequest);
        return checkResponse(responseObj, status);
    }

    private static <T> T httpMethodReturnType(int type, ClientRequest restRequest, MediaType mediaType, int status, Class<T> responseType) {
        ClientResponse<?> responseObj = httpMethodReturnResponse(type, restRequest, mediaType, status);
        return getResponseEntity(responseObj, responseType);
    }

    private static final int GET = 0;
    private static final int POST = 1;
    private static final int DEL = 2;

    private static ClientResponse<?> logAndExecuteRequest( int type, ClientRequest restRequest) { 
        String typeName;
        switch( type ) { 
        case GET: 
            typeName = "GET";
            break;
        case POST:
            typeName = "POST";
            break;
        case DEL:
            typeName = "DEL";
            break;
        default:
            throw new IllegalStateException("Unknown HTTP method type: " + type );
        }
        try { 
            logger.debug(">> [{} {}] {}", typeName,  restRequest.getHeaders().getFirst(HttpHeaderNames.ACCEPT), restRequest.getUri());
        } catch( Exception e ) {
            logger.error( "Unable to log information about rest request:  {}", e.getMessage(), e);
            fail("Unable to log information about rest request; see log and stack trace");
        }
        
        ClientResponse<?> response = null;
        try { 
            switch( type ) { 
            case GET: 
                response = restRequest.get();
                break;
            case POST:
                response = restRequest.post();
                break;
            case DEL:
                response = restRequest.delete();
                break;
            default:
                throw new IllegalStateException("Unknown HTTP method type: " + type );
            }
        } catch (Exception e ) { 
            logger.error( "Unable to execute rest request:  {}", e.getMessage(), e);
            fail("Unable to log information about rest request; see log and stack trace");
        }
        return response;
    }

    private static <T extends Object> T getResponseEntity(ClientResponse<?> responseObj, Class<T> responseType ) { 
        T responseEntity = null;
        try { 
            responseEntity = responseObj.getEntity(responseType);
        } catch( Exception e ) { 
            String msg = "Unable to serialize " + responseType.getSimpleName() + " instance";
            responseObj.resetStream();
            logger.error("{}:\n {}", msg, responseObj.getEntity(String.class), e);
            fail(msg);
            throw new RuntimeException("Fail should keep this exception from being thrown!");
        }
        return responseEntity;
    }
    
    private static ClientResponse<?> checkResponse(ClientResponse<?> responseObj, int status) {
        responseObj.resetStream();
        int reqStatus = responseObj.getStatus();
        if (reqStatus != status) {
            logger.warn("Response with exception:\n" + responseObj.getEntity(String.class));
            fail("Incorrect status: " + reqStatus);
        }
        String contentType = (String) responseObj.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if( contentType != null ) { 
            if( ! (contentType.startsWith(MediaType.APPLICATION_XML)) && ! (contentType.startsWith(MediaType.APPLICATION_JSON)) ) { 
               logger.warn("Incorrect format for response: " + contentType + "\n" + responseObj.getEntity(String.class) );
               fail("Incorrect response media type: " + contentType );
            }
        }
        return responseObj;
    }

    public static void setAcceptHeader(ClientRequest restRequest, MediaType mediaType) { 
        assertNotNull( "Null media type.", mediaType );
        MultivaluedMap<String, String> headers = restRequest.getHeaders();
        headers.putSingle(HttpHeaderNames.ACCEPT, mediaType.getType() + "/" + mediaType.getSubtype());
        assertNotNull( "Null ACCEPT headers!", headers.get(HttpHeaderNames.ACCEPT));
        assertEquals( "Multiple ACCEPT headers!", 1, headers.get(HttpHeaderNames.ACCEPT).size());
    }

    public static long restCallDurationLimit = 500;
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    public static ClientResponse<?> checkTimeResponse(ClientResponse<?> responseObj) throws Exception {
        long start = System.currentTimeMillis();
        try { 
            return checkResponse(responseObj, 202); 
        } finally { 
           long duration = System.currentTimeMillis() - start;
           assertTrue( "Rest call took too long: " + duration + "ms", duration < restCallDurationLimit);
           logger.info("Op time : " + sdf.format(new Date(duration)));
        }
    }

    public static ClientResponse<?> checkResponsePostTime(ClientRequest restRequest, MediaType mediaType, int status) throws Exception {
        setAcceptHeader(restRequest, mediaType);
        long before, after;
        logger.debug("BEFORE: " + sdf.format((before = System.currentTimeMillis())));
        ClientResponse<?> responseObj = checkResponse(restRequest.post(), status);
        logger.debug("AFTER: " + sdf.format((after = System.currentTimeMillis())));
        long duration = (after - before);
        assertTrue("Call took longer than " + restCallDurationLimit / 1000 + " seconds: " + duration + "ms", duration < restCallDurationLimit);
        return responseObj;
    }
   
}