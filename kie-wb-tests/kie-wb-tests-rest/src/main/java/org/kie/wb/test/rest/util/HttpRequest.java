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

package org.kie.wb.test.rest.util;

import java.io.IOException;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.spi.ReaderException;
import org.kie.wb.test.rest.exception.BadRequestException;
import org.kie.wb.test.rest.exception.ForbiddenException;
import org.kie.wb.test.rest.exception.NotFoundException;
import org.kie.wb.test.rest.exception.RemoteException;
import org.kie.wb.test.rest.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest<T> {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final ClientRequest request;
    private final Class<T> returnType;

    private Object body;
    private MediaType contentType;

    public HttpRequest(ClientRequest request, Class<T> returnType, MediaType contentType) {
        this.request = request;
        this.returnType = returnType;
        this.contentType = contentType;
    }

    public HttpRequest<T> contentType(MediaType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpRequest<T> body(Object body) {
        this.body = serializeEntity(body);
        return this;
    }

    public T get() {
        return call(() -> request.get(returnType));
    }

    public T post() {
        if (body != null) {
            request.body(contentType, body);
        }
        return call(() -> request.post(returnType));
    }

    public T delete() {
        return call(() -> request.delete(returnType));
    }

    private T call(HttpMethod<T> httpMethod) {
        request.accept(contentType);

        ClientResponse<T> response = null;
        try {
            response = sendRequest(httpMethod);
            checkResponse(response);
            return response.getEntity();
        } finally {
            if (response != null) {
                response.releaseConnection();
            }
        }
    }

    private ClientResponse<T> sendRequest(HttpMethod<T> httpMethod) {
        try {
            return httpMethod.sendRequest();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void checkResponse(ClientResponse<T> originalResponse) {
        ClientResponse<?> response = BaseClientResponse.copyFromError(originalResponse);
        originalResponse.resetStream();

        String entity = "";
        try {
            entity = response.getEntity(String.class);
        } catch (ReaderException ex) {
            // sometimes the entity is empty and we do not want to see this exception at all
        }

        switch (response.getResponseStatus()) {
            case OK:
            case CREATED:
            case ACCEPTED:
            case NO_CONTENT:
                log.info(entity);
                return;
            case BAD_REQUEST:
                throw new BadRequestException(entity);
            case UNAUTHORIZED:
                throw new UnauthorizedException(entity);
            case FORBIDDEN:
                throw new ForbiddenException(entity);
            case NOT_FOUND:
                throw new NotFoundException(entity);
            default:
                throw new RemoteException(response.getResponseStatus().getStatusCode(), entity);
        }
    }

    private String serializeEntity(Object entity) {
        try {
            return new ObjectMapper().writeValueAsString(entity);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to serialize " + entity.getClass().getSimpleName(), ex);
        }
    }

    @FunctionalInterface
    private interface HttpMethod<T> {

        ClientResponse<T> sendRequest() throws Exception;

    }

}
