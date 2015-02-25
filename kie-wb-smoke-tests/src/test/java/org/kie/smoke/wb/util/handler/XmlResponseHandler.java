package org.kie.smoke.wb.util.handler;

import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.entity.ContentType;

public class XmlResponseHandler<T,P> extends AbstractResponseHandler<T, P> {

    private JAXBContext jaxbContext = null;
    
    public XmlResponseHandler(int status, Class<T>... returnTypes) { 
        super(ContentType.APPLICATION_XML, status, returnTypes);
    }
   
    public XmlResponseHandler(Class<T>... returnTypes) { 
        super(ContentType.APPLICATION_XML, returnTypes);
    }
 
    protected T deserialize(Reader reader) {
        JAXBContext jaxbContext = getJaxbContext();
        
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch( JAXBException jaxbe ) { 
            throw new IllegalStateException("Unable to create unmarshaller", jaxbe);
        }

        Object jaxbObj = null;
        try { 
            jaxbObj = unmarshaller.unmarshal(reader);
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
            List<Class> types = new ArrayList<Class>(2);
            types.add(returnType);
            if( parameterType != null ) { 
                types.add(this.parameterType);
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
        Class [] types = { entity.getClass() };
        try { 
            jaxbContext = JAXBContext.newInstance(types);
        } catch( JAXBException jaxbe ) { 
            throw new IllegalStateException("Unable to create JAXBContext", jaxbe);
        } 
        
        Marshaller marshaller = null;
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch( JAXBException jaxbe ) { 
            throw new IllegalStateException("Unable to create unmarshaller", jaxbe);
        }
        
        StringWriter xmlStrWriter = new StringWriter();
        try { 
            marshaller.marshal(entity, xmlStrWriter);
        } catch( JAXBException jaxbe ) { 
           throw new IllegalStateException("Unable to marshal " + entity.getClass().getSimpleName() + " instance", jaxbe);
        }

        return xmlStrWriter.toString();
    }
    
}
