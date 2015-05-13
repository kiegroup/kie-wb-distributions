package org.kie.smoke.wb.util.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public abstract class AbstractResponseHandler<T,P> implements ResponseHandler<T> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractResponseHandler.class);
    
    protected final Class<T> returnType;
    protected Class<P> parameterType = null;
    private int status = 200;
  
    protected final ContentType myContentType;
    
    public AbstractResponseHandler(ContentType type, int status, Class<T>... returnTypes) {
        this.myContentType = type;
        this.status = status;
        if( returnTypes != null && returnTypes.length > 0 ) { 
            this.returnType = returnTypes[0];
            if( returnTypes.length == 2 ) { 
                this.parameterType = (Class<P>) returnTypes[1];
            }
        } else { 
            this.returnType = null;
        }
    }
   
    public AbstractResponseHandler(ContentType type, Class<T>... returnTypes) {
        this.myContentType = type;
        if( returnTypes != null && returnTypes.length > 0 ) { 
            this.returnType = returnTypes[0];
            if( returnTypes.length == 2 ) { 
                this.parameterType = (Class<P>) returnTypes[1];
            }
        } else { 
            this.returnType = null;
        }
    }
    
    @Override
    public T handleResponse( HttpResponse response ) throws ClientProtocolException, IOException {
        int responseStatus = response.getStatusLine().getStatusCode();
        
        HttpEntity entity = response.getEntity();
        assertNotNull("Empty response content", entity);
       
        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();
        
        InputStream contentStream = entity.getContent();
        Reader reader;
        if( charset != null ) { 
            reader = new InputStreamReader(contentStream, charset);
        } else { 
            reader = new InputStreamReader(contentStream);
        }
        reader = new BufferedReader(reader);
      
        if( status != responseStatus ) { 
            if( ! myContentType.equals(contentType) ) { 
                if( contentType.toString().contains("text/html") ) { 
                    StringWriter writer = new StringWriter();
                    char[] buffer = new char[1024];
                    for (int n; (n = reader.read(buffer)) != -1; ) {
                        writer.write(buffer, 0, n);
                    }
                    String content = writer.toString();
                    // now that we know that the result is wrong, try to identify the reason
                    Document doc = Jsoup.parse(content);
                    String errorBody = doc.body().text();
                    fail( responseStatus + ": " + errorBody + " [expected " + status + "]" );
                }  else { 
                    assertEquals("Response status [content type: " + contentType.toString() + "]", status, responseStatus);
                }
            } else if( contentType.toString().contains("text/plain") ) { 
                    StringWriter writer = new StringWriter();
                    char[] buffer = new char[1024];
                    for (int n; (n = reader.read(buffer)) != -1; ) {
                        writer.write(buffer, 0, n);
                    }
                    String errorBody = writer.toString();
                    // now that we know that the result is wrong, try to identify the reason
                    fail( responseStatus + ": " + errorBody + " [expected " + status + "]" );
            } else { 
                assertEquals("Response status", status, responseStatus);
            }
        } 

        char[] arr = new char[8 * 1024];
        StringBuilder buffer = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
            buffer.append(arr, 0, numCharsRead);
        }
        reader.close();
        
        if( returnType != null ) {
            return deserialize(buffer.toString());
        } else { 
            return null;
        }
    }

    protected abstract T deserialize(String content);
    
    public abstract String serialize(Object entity);
    
}
