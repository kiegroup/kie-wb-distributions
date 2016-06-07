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

import java.net.MalformedURLException;
import java.net.URL;
import javax.ws.rs.core.MediaType;

import org.apache.commons.net.util.Base64;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestFactory {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestFactory.class);

    private final String url;
    private final MediaType defaultContentType;

    private final String authHeader;
    private final ClientRequestFactory clientRequestFactory;


    public HttpRequestFactory(String url, String userId, String password, MediaType defaultContentType) {
        this.url = url;
        this.defaultContentType = defaultContentType;

        this.authHeader = createAuthHeader(userId, password);
        this.clientRequestFactory = createClientRequestFactory(userId, password);
    }

    private static String createAuthHeader(String userId, String password) {
        return "Basic " + Base64.encodeBase64String(String.format("%s:%s", userId, password).getBytes()).trim();
    }

    private static ClientRequestFactory createClientRequestFactory(String userId, String password) {
        CredentialsProvider cp = new BasicCredentialsProvider();
        cp.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(userId, password));

        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(cp)
                .build();
        ClientExecutor clientExecutor = new ApacheHttpClient4Executor(httpClient);

        return new ClientRequestFactory(clientExecutor, ResteasyProviderFactory.getInstance());
    }

    private ClientRequest createClientRequest(String path) {
        try {
            String address = new URL(url + path).toExternalForm();
            log.debug(address);

            return clientRequestFactory.createRequest(address)
                    .header("Authorization", authHeader)
                    .followRedirects(true);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid path", ex);
        }
    }

    public <T> HttpRequest<T> request(String path, Class<T> returnType) {
        return new HttpRequest<>(createClientRequest(path), returnType, defaultContentType);
    }

}
