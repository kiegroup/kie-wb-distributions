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

package org.kie.smoke.wb.base.util.handler;

import static org.kie.smoke.wb.base.util.RestUtil.logAndFail;

import org.apache.http.entity.ContentType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

@SuppressWarnings("unchecked")
public class JsonResponseHandler<T,P> extends AbstractResponseHandler<T, P> {

    public JsonResponseHandler(int status, Class<T>... returnTypes) { 
        super(ContentType.APPLICATION_JSON, status, returnTypes);
    }
    
    public JsonResponseHandler(Class<T>... returnTypes) { 
        super(ContentType.APPLICATION_JSON, returnTypes);
    }
    
    private final ObjectMapper om = new ObjectMapper();
    
    @Override
    protected T deserialize(String content) {
        if( parameterType == null ) { 
            try {
                T obj =  om.readValue(content, returnType);
                if( logger.isTraceEnabled() ) { 
                   Object prettyPrintObj = om.readValue(content, Object.class);
                   String prettyContent = om.writerWithDefaultPrettyPrinter().writeValueAsString(prettyPrintObj);
                   logger.trace("JSON < |\n{}", prettyContent );
                }
                return obj;
            } catch( Exception e ) {
               logAndFail(returnType.getSimpleName() + " deserialization failed", e); 
            } 
        } else { 
            JavaType genericsType = om.getTypeFactory().constructParametricType(this.returnType, this.parameterType);
            try { 
                return (T) om.readValue(content, genericsType);
            } catch( Exception e ) {
               logAndFail(returnType.getSimpleName() + "<" + parameterType.getSimpleName() + ">" + " deserialization failed", e); 
            } 
        }
        
        // never happens
        return null;
    }

    @Override
    public String serialize( Object entity ) {
        String out = null;
        try {
            out = om.writeValueAsString(entity);
            if( logger.isTraceEnabled() ) { 
                logger.trace("JSON > |\n{} ", om.writerWithDefaultPrettyPrinter().writeValueAsString(entity) );
            }
        } catch( Exception e ) {
            logAndFail(entity.getClass().getSimpleName() + " instance serialization failed", e); 
        } 
        
        return out;
    }
    
    
}
