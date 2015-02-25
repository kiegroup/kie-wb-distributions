package org.kie.smoke.wb.util.handler;

import static org.kie.smoke.wb.util.RestUtil.failAndLog;

import java.io.Reader;

import org.apache.http.entity.ContentType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

public class JsonResponseHandler<T,P> extends AbstractResponseHandler<T, P> {

    public JsonResponseHandler(int status, Class<T>... returnTypes) { 
        super(ContentType.APPLICATION_JSON, status, returnTypes);
    }
    
    public JsonResponseHandler(Class<T>... returnTypes) { 
        super(ContentType.APPLICATION_JSON, returnTypes);
    }
    
    private final ObjectMapper om = new ObjectMapper();
    
    protected T deserialize(Reader reader) {
        Object type = returnType;
        if( parameterType == null ) { 
            try {
                return om.readValue(reader, returnType);
            } catch( Exception e ) {
               failAndLog(returnType.getSimpleName() + " deserialization failed", e); 
            } 
        } else { 
            JavaType genericsType = om.getTypeFactory().constructParametricType(this.returnType, this.parameterType);
            try { 
                return (T) om.readValue(reader, genericsType);
            } catch( Exception e ) {
               failAndLog(returnType.getSimpleName() + "<" + parameterType.getSimpleName() + ">" + " deserialization failed", e); 
            } 
        }
        
        // never happens
        return null;
    }

    @Override
    public String serialize( Object entity ) {
        try {
            return om.writeValueAsString(entity);
        } catch( Exception e ) {
            failAndLog(entity.getClass().getSimpleName() + " instance serialization failed", e); 
        } 
        
        // never happens
        return null;
    }
    
    
}
