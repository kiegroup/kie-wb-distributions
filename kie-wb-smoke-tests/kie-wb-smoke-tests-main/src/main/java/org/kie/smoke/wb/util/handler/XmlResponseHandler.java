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

package org.kie.smoke.wb.util.handler;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.entity.ContentType;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

@SuppressWarnings("unchecked")
public class XmlResponseHandler<T,P> extends AbstractResponseHandler<T, P> {

    private JAXBContext jaxbContext = null;
    private List<Class> extraJaxbClasses = new ArrayList<Class>(0);
    
    public XmlResponseHandler(int status, Class<T>... returnTypes) { 
        super(ContentType.APPLICATION_XML, status, returnTypes);
    }
   
    public XmlResponseHandler(Class<T>... returnTypes) { 
        super(ContentType.APPLICATION_XML, returnTypes);
    }
 
    public void addExtraJaxbClasses( Class... extraClass ) { 
        this.extraJaxbClasses.addAll(Arrays.asList(extraClass));
    }
   
    @Override
    protected T deserialize(String content) {

        if( logger.isTraceEnabled() ) { 
            try {
                Document doc = DocumentHelper.parseText(content);  
                StringWriter sw = new StringWriter();  
                OutputFormat format = OutputFormat.createPrettyPrint();  
                XMLWriter xw = new XMLWriter(sw, format);  
                xw.write(doc);
                String prettyContent = sw.toString();
                logger.trace("XML  < |\n{}", prettyContent );
            } catch( IOException ioe ) {
                logger.error( "Unabel to write XML document: " + ioe.getMessage(), ioe );
            } catch( DocumentException de ) {
                logger.error( "Unabel to parse text: " + de.getMessage(), de );
            }  
        }

        JAXBContext jaxbContext = getJaxbContext();
        
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch( JAXBException jaxbe ) { 
            throw new IllegalStateException("Unable to create unmarshaller", jaxbe);
        }

        ByteArrayInputStream contentStream = new ByteArrayInputStream(content.getBytes());
        Object jaxbObj = null;
        try { 
            jaxbObj = unmarshaller.unmarshal(contentStream);
        } catch( JAXBException jaxbe ) { 
           throw new IllegalStateException("Unable to unmarshal reader", jaxbe);
        }

        Class returnedClass = jaxbObj.getClass();
        assertTrue( returnedClass.getSimpleName() + " received instead of " + this.returnType.getSimpleName(),
                returnType.isAssignableFrom(returnedClass) );
        return (T) jaxbObj;
    }

    private JAXBContext getJaxbContext() {
        if( jaxbContext == null ) { 
            Set<Class> types = new HashSet<Class>(2);
            types.add(returnType);
            if( parameterType != null ) { 
                types.add(this.parameterType);
            } 
            if( ! extraJaxbClasses.isEmpty() ) { 
               types.addAll(extraJaxbClasses);
            }
            
            try { 
                jaxbContext = JAXBContext.newInstance(types.toArray(new Class[types.size()]));
            } catch( JAXBException jaxbe ) { 
                throw new IllegalStateException("Unable to create JAXBContext", jaxbe);
            } 
        }
        return jaxbContext;
    }
    
    @Override
    public String serialize( Object entity ) {
        JAXBContext jaxbContext;
        List<Class> typeList = new ArrayList<Class>();
        typeList.add(entity.getClass());
        
        if( ! extraJaxbClasses.isEmpty() ) { 
            typeList.addAll(extraJaxbClasses);
        } 
        
        try { 
            jaxbContext = JAXBContext.newInstance(typeList.toArray(new Class[typeList.size()]));
        } catch( JAXBException jaxbe ) { 
            throw new IllegalStateException("Unable to create JAXBContext", jaxbe);
        } 
        
        return serialize(entity, jaxbContext); 
    }
   
    public String serialize( Object entity, JAXBContext jaxbContext ) {
        Marshaller marshaller = null;
        try {
            marshaller = jaxbContext.createMarshaller();
            if( logger.isTraceEnabled() ) { 
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            }
        } catch( JAXBException jaxbe ) { 
            throw new IllegalStateException("Unable to create unmarshaller", jaxbe);
        }
        
        StringWriter xmlStrWriter = new StringWriter();
        try { 
            marshaller.marshal(entity, xmlStrWriter);
        } catch( JAXBException jaxbe ) { 
           throw new IllegalStateException("Unable to marshal " + entity.getClass().getSimpleName() + " instance", jaxbe);
        }

        String out = xmlStrWriter.toString(); 
        logger.trace("XML  > |\n{}", out );
        return out;
    }
    
}
