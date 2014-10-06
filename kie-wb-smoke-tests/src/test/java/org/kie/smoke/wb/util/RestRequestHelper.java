package org.kie.smoke.wb.util;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.kie.services.client.api.command.exception.RemoteCommunicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is meant to help users interact with the (kie-wb or business-central) REST api by creating
 * either {@link ClientRequest} (REST request) instances or {@link ClientRequestFactory} instances to
 * create {@link ClientRequest} instances.
 */
public class RestRequestHelper {

    private ClientRequestFactory requestFactory;

    // Just for building/config, not for use
    private URL serverPlusRestUrl = null;
    private MediaType type = null;
    private String username = null;
    private String password = null;
    private int timeout = 5;
    private boolean formBasedAuth = false;

    private static int DEFAULT_TIMEOUT = 5;
    public static AtomicInteger idGen = new AtomicInteger(1);
    
    /**
     * Helper methods
     */

    private URL addRestToPath( URL origUrl ) {
        StringBuilder urlString = new StringBuilder(origUrl.toExternalForm());
        if( !urlString.toString().endsWith("/") ) {
            urlString.append("/");
        }
        urlString.append("rest/");
        serverPlusRestUrl = convertStringToUrl(urlString.toString());
        return serverPlusRestUrl;
    }

    private static URL convertStringToUrl( String urlString ) {
        URL realUrl;
        try {
            realUrl = new URL(urlString);
        } catch( MalformedURLException murle ) {
            throw new IllegalArgumentException("URL (" + urlString + ") is incorrectly formatted: " + murle.getMessage(), murle);
        }
        return realUrl;
    }

    private RestRequestHelper() {

    }

    /**
     * Creates a {@link RestRequestHelper} instance.
     * 
     * @param serverPortUrl in the format of "http://server:port/"
     * @param username The username (registered on the kie-wb or business-central server)
     * @param password The password associated with the username
     * @param timeout The timeout used for REST requests
     * @param mediaType The media type used for REST requests
     * @param formBasedAuth Whether the request should use form based authentication (only recommended for tomcat instances)
     */
    public static RestRequestHelper newInstance( URL serverPortUrl, String username, String password, int timeout,
            MediaType mediaType, boolean formBasedAuth ) {
        RestRequestHelper inst = new RestRequestHelper();
        URL serverPlusRestUrl = inst.addRestToPath(serverPortUrl);
        if( formBasedAuth ) {
            inst.requestFactory = createFormBasedAuthenticatingRequestFactory(serverPlusRestUrl, username, password, timeout);
        } else {
            inst.requestFactory = createAuthenticatingRequestFactory(serverPlusRestUrl, username, password, timeout);
        }
        inst.type = mediaType;
        inst.username = username;
        inst.password = password;
        return inst;
    }

    public static RestRequestHelper newInstance( URL serverPortUrl, String username, String password, int timeout,
            MediaType mediaType ) {
        return newInstance(serverPortUrl, username, password, timeout, mediaType, false);
    }

    /**
     * Creates a {@link RestRequestHelper} instance.
     * 
     * @param serverPortUrl in the format of "http://server:port/"
     * @param username The username (registered on the kie-wb or business-central server)
     * @param password The password associated with the username.
     * @param timeout The timeout used for REST requests.
     */
    public static RestRequestHelper newInstance( URL serverPortUrl, String username, String password, int timeout ) {
        return newInstance(serverPortUrl, username, password, timeout, null);
    }

    /**
     * Creates a {@link RestRequestHelper} instance. A default timeout of 5 seconds is used for REST requests.
     * 
     * @param serverPortUrl in the format of "http://server:port/"
     * @param username The username (registered on the kie-wb or business-central server)
     * @param password The password associated with the username.
     * 
     */
    public static RestRequestHelper newInstance( URL serverPortUrl, String username, String password ) {
        return newInstance(serverPortUrl, username, password, DEFAULT_TIMEOUT, null);
    }

    public RestRequestHelper setMediaType( MediaType type ) {
        this.type = type;
        return this;
    }

    public MediaType getMediaType() {
        return this.type;
    }

    public RestRequestHelper setTimeout( int timeout ) {
        this.timeout = timeout;
        this.requestFactory = createAuthenticatingRequestFactory(serverPlusRestUrl, username, password, timeout);
        return this;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public RestRequestHelper setFormBasedAuth( boolean useFormBasedAuth ) {
        this.formBasedAuth = useFormBasedAuth;
        return this;
    }

    public boolean getFormBasedAuth() {
        return this.formBasedAuth;
    }

    /**
     * Creates a REST request for the given REST operation URL.
     * </p>
     * For example, if you wanted to create a REST request for the following URL:
     * <ul>
     * <li><code>http://my.server.com:8080/rest/runtime/test-deploy/process/org.jbpm:HR:1.0/start</code></li>
     * </ul>
     * Then you could call this method as follows: <code>
     * restRequestHelperInstance.createRequest( "runtime/test-deploy/process/org.jbpm:HR:1.0/start" );
     * </code>
     * 
     * @param restOperationUrl The URL of the REST operation, exculding the server/port and "/rest" base.
     * @return A {@link ClientRequest} instance that authenticates based on the username/password arguments
     * given to the constructor of this {@link RestRequestHelper} instance.
     */
    public ClientRequest createRequest( String restOperationUrl ) {
        if( restOperationUrl.startsWith("/") ) {
            restOperationUrl = restOperationUrl.substring(1);
        }
        ClientRequest request = requestFactory.createRelativeRequest(restOperationUrl);
        if( type != null ) {
            request.accept(type);
        }
        return request;
    }

    /**
     * This method creates a {@link ClientRequestFactory} instance that can be used to create {@link ClientRequest} instances
     * that will authenticate against a kie-wb or business-central server using the given username and password.
     * </p>
     * The {@link ClientRequestFactory} instance can then be used like this to create {@link ClientRequest} REST request instances:
     * 
     * <pre>
     * {@link ClientRequestFactory} requestFactory = {@link RestRequestHelper}.createRequest( "http://my.server:8080/rest", "user", "pass", 10);
     * {@link ClientRequest} restRequest = requestFactory.createRelativeRequest( "task/2/start" );
     * ClientResponse restResponse =  restRequest.post();
     * // do something with the response
     * </pre>
     * 
     * @param restBaseUrl The base URL of the rest server, which should have this format: "http://server[:port]/rest".
     * @param username The username to use when authenticating.
     * @param password The password to use when authenticating.
     * @param timeout The timeout to use for the REST request.
     * @return A {@link ClientRequestFactory} in order to create REST request ( {@link ClientRequest} ) instances
     * to interact with the REST api.
     */
    public static ClientRequestFactory createRequestFactory( URL restBaseUrl, String username, String password, int timeout ) {
        return createAuthenticatingRequestFactory(restBaseUrl, username, password, timeout);
    }

    /**
     * See {@link RestRequestHelper#createRequestFactory(String, String, String, int)}. This method uses a default timeout of
     * 5 seconds, whereas the referred method allows users to pass the value for the timeout.
     * 
     * @param restBaseUrl The base URL of the rest server, which should have this format: "http://server[:port]/rest".
     * @param username The username to use when authenticating.
     * @param password The password to use when authenticating.
     * @return A {@link ClientRequestFactory} in order to create REST request ( {@link ClientRequest} ) instances
     * to interact with the REST api.
     */
    public static ClientRequestFactory createRequestFactory( URL restBaseUrl, String username, String password ) {
        return createAuthenticatingRequestFactory(restBaseUrl, username, password, DEFAULT_TIMEOUT);
    }

    public static ClientRequestFactory createRequestFactory( URL restBaseUrl, String username, String password,
            boolean useFormBasedAuth ) {
        if( useFormBasedAuth ) {
            return createFormBasedAuthenticatingRequestFactory(restBaseUrl, username, password, DEFAULT_TIMEOUT);
        } else {
            return createAuthenticatingRequestFactory(restBaseUrl, username, password, DEFAULT_TIMEOUT);
        }
    }

    public static ClientRequestFactory createRequestFactory( URL restBaseUrl, String username, String password, int timeout,
            boolean useFormBasedAuth ) {
        if( useFormBasedAuth ) {
            return createFormBasedAuthenticatingRequestFactory(restBaseUrl, username, password, timeout);
        } else {
            return createAuthenticatingRequestFactory(restBaseUrl, username, password, timeout);
        }
    }

    /**
     * Creates an request factory that authenticates using the given username and password
     *
     * @param url
     * @param username
     * @param password
     * @param timeout
     *
     * @return A request factory that can be used to send (authenticating) requests to REST services
     */
    public static ClientRequestFactory createAuthenticatingRequestFactory( URL url, String username, String password, int timeout ) {
        BasicHttpContext localContext = new BasicHttpContext();
        HttpClient preemptiveAuthClient = createPreemptiveAuthHttpClient(username, password, timeout, localContext);
        ClientExecutor clientExecutor = new ApacheHttpClient4Executor(preemptiveAuthClient, localContext);
        try {
            return new ClientRequestFactory(clientExecutor, url.toURI());
        } catch( URISyntaxException urise ) {
            throw new IllegalArgumentException("URL (" + url.toExternalForm() + ") is not formatted correctly.", urise);
        }
    }

    /**
     * Creates an request factory that authenticates using the given username and password
     *
     * @param url
     * @param username
     * @param password
     * @param timeout
     *
     * @return A request factory that can be used to send (authenticating) requests to REST services
     */
    public static ClientRequestFactory createFormBasedAuthenticatingRequestFactory( URL url, final String username,
            final String password, int timeout ) {
        try {
            return new FormBasedAuthenticatingClientRequestFactory(url.toURI(), username, password, timeout);
        } catch( URISyntaxException urise ) {
            throw new RemoteCommunicationException("Invalid URL: " + url.toExternalForm(), urise);
        }
    }

    static class FormBasedAuthenticatingClientRequestFactory extends ClientRequestFactory {
        private final String username;
        private final String password;
        private final ClientExecutor executor;

        public FormBasedAuthenticatingClientRequestFactory(URI uri, String username, String password, int timeout) {
            super(uri);
            this.username = username;
            this.password = password;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, timeout * 1000);
            HttpConnectionParams.setSoTimeout(params, timeout * 1000);
            executor = new ApacheHttpClient4Executor(httpClient);
        }

        public ClientRequest createRelativeRequest( String uriTemplate ) {
            ClientRequest request = executor.createRequest(getBase().toString() + uriTemplate);
            request.registerInterceptor(new FormBasedAuthenticatingInterceptor(username, password));
            return request;
        }

        public ClientRequest createRequest( String uriTemplate ) {
            ClientRequest request = executor.createRequest(uriTemplate);
            request.registerInterceptor(new FormBasedAuthenticatingInterceptor(username, password));
            return request;
        }
    }

    static class FormBasedAuthenticatingInterceptor implements ClientExecutionInterceptor {
        private static final Logger logger = LoggerFactory.getLogger(FormBasedAuthenticatingInterceptor.class);
        private static final String LOGIN_FORM = "/j_security_check";
        private static final String FORM_BASED_AUTH_PROPERTY = "org.kie.remote.form.based.auth";
        private final String username;
        private final String password;
        private String sessionCookie = null;

        public FormBasedAuthenticatingInterceptor(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * This method is called </b>every time</b> a {@link ClientRequest} is executed.
         * </p>
         * This interceptor method is thus triggered from {@link ClientRequest} calls within the method itself,
         * making it a recursively called method [*].
         */
        @Override
        public ClientResponse<?> execute( ClientExecutionContext ctx ) throws Exception {
            // Setup
            ClientRequest origRequest = ctx.getRequest();
            if( sessionCookie != null ) {
                // Try with session cookie if it already exists
                origRequest.header(HttpHeaders.COOKIE, sessionCookie);
            }
            URL restUrl = new URL(origRequest.getUri());
            String restUrlString = restUrl.toExternalForm();
            String origRequestMethod = origRequest.getHttpMethod();
            debug("Processing request: [" + origRequestMethod + "] " + restUrlString
                    + (sessionCookie == null ? "" : " (session: " + sessionCookie + ")"));
            // Do request (whichever request it may be!)
            ClientResponse<?> response = ctx.proceed();
            int status = response.getStatus();
            debug("Response received [" + status + "]");
            // If
            // 1. this is the form-based auth request, or
            // 2. if the form-based auth has completed,
            // then we're done..
            if( restUrlString.endsWith(LOGIN_FORM)
                    || Boolean.parseBoolean((String) origRequest.getAttributes().get(FORM_BASED_AUTH_PROPERTY)) ) {
                return response;
            }
            // Check response to see if form-based auth is necessary
            String requestCookie = (String) response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            Object contentTypeObj = response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            boolean doFormBasedAuth = false;
            if( contentTypeObj != null && (contentTypeObj instanceof String) ) {
                if( ((String) contentTypeObj).startsWith(MediaType.TEXT_HTML) && requestCookie != null
                        && !requestCookie.equals(sessionCookie) ) {
                    debug("New session cookie: " + requestCookie);
                    doFormBasedAuth = true;
                    sessionCookie = requestCookie;
                }
            }
            // If form-based auth is required, do it
            if( doFormBasedAuth ) {
                response.releaseConnection();
                // Create form-based auth URL
                String appBase = "/" + restUrl.getPath().substring(1).replaceAll("/.*", "");
                URL appBaseUrl = new URL(restUrl.getProtocol(), restUrl.getHost(), restUrl.getPort(), appBase);
                ClientRequestFactory requestFactory = new ClientRequestFactory(appBaseUrl.toURI());
                ClientRequest formRequest = requestFactory.createRelativeRequest(LOGIN_FORM);
                formRequest = formRequest.formParameter("j_username", username).formParameter("j_password", password);
                if( sessionCookie != null ) {
                    formRequest.header(HttpHeaders.COOKIE, sessionCookie);
                }
                // Do form-based auth
                try {
                    debug("Trying form-based authentication for session '" + sessionCookie + "'");
                    // [*] triggers recursive call of this method
                    response = formRequest.post();
                    int formRequestStatus = response.getStatus();
                    if( formRequestStatus != 302 ) {
                        String errMsg = "Unable to complete form-based authentication in via " + formRequest.getUri();
                        System.err.println(errMsg + "\n [" + formRequestStatus + "] " + response.getEntity(String.class));
                        throw new RemoteCommunicationException(errMsg + " (see output)");
                    }
                    debug("Form-based authentication succeeded.");
                } catch( RemoteCommunicationException rce ) {
                    throw rce;
                } catch( Exception e ) {
                    if( e instanceof RuntimeException ) {
                        throw (RuntimeException) e;
                    } else {
                        String errMsg = "Unable to complete form-based authentication in via " + formRequest.getUri();
                        throw new RemoteCommunicationException(errMsg, e);
                    }
                } finally {
                    try {
                        response.releaseConnection();
                    } catch( Exception e ) {
                        // do nothing..
                    }
                }
                // Somehow, query parameters are being added a second time here..
                // As long we use UriInfo in the service-side resources to get the query parameters
                // instead of the HttpServletRequest, then things work..
                try {
                    if( sessionCookie == null ) {
                        throw new IllegalStateException("A cookie for a authenticated session should be available at this point!");
                    }
                    debug("Retrying original request (proceed): [" + origRequestMethod + "] " + restUrlString);
                    // [*] triggers recursive call of this method
                    response = ctx.proceed();
                } catch( Exception e ) {
                    if( e instanceof RuntimeException ) {
                        throw (RuntimeException) e;
                    } else {
                        throw new RemoteCommunicationException("Unable to " + origRequestMethod + " to " + restUrlString, e);
                    }
                }
            }
            return response;
        }

        private void debug( String msg ) {
            logger.debug(msg);
        }
    }

    /**
     * This method is used in order to create the authenticating REST client factory.
     *
     * @param userName
     * @param password
     * @param timeout
     * @param localContext
     *
     * @return A {@link DefaultHttpClient} instance that will authenticate using the given username and password.
     */
    private static DefaultHttpClient createPreemptiveAuthHttpClient( String userName, String password, int timeout,
            BasicHttpContext localContext ) {
        BasicHttpParams params = new BasicHttpParams();
        int timeoutMilliSeconds = timeout * 1000;
        HttpConnectionParams.setConnectionTimeout(params, timeoutMilliSeconds);
        HttpConnectionParams.setSoTimeout(params, timeoutMilliSeconds);
        DefaultHttpClient client = new DefaultHttpClient(params);
        if( userName != null && !"".equals(userName) ) {
            client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(userName, password));
            // Generate BASIC scheme object and stick it to the local execution context
            BasicScheme basicAuth = new BasicScheme();
            String contextId = UUID.randomUUID().toString();
            localContext.setAttribute(contextId, basicAuth);
            // Add as the first request interceptor
            client.addRequestInterceptor(new PreemptiveAuth(contextId), 0);
        }
        String hostname = "localhost";
        try {
            hostname = Inet6Address.getLocalHost().toString();
        } catch( Exception e ) {
            // do nothing
        }
        // set the following user agent with each request
        String userAgent = "org.kie.services.client (" + idGen.incrementAndGet() + " / " + hostname + ")";
        HttpProtocolParams.setUserAgent(client.getParams(), userAgent);
        return client;
    }

    /**
     * This class is used in order to effect preemptive authentication in the REST request factory.
     */
    static class PreemptiveAuth implements HttpRequestInterceptor {
        private final String contextId;

        public PreemptiveAuth(String contextId) {
            this.contextId = contextId;
        }

        public void process( final HttpRequest request, final HttpContext context ) throws HttpException, IOException {
            AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
            // If no auth scheme available yet, try to initialize it preemptively
            if( authState.getAuthScheme() == null ) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute(contextId);
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                if( authScheme != null ) {
                    Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                    if( creds == null ) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.update(authScheme, creds);
                }
            }
        }
    }
}
