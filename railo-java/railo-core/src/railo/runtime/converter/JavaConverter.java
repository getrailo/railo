package railo.runtime.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.coder.Base64Coder;
import railo.runtime.coder.CoderException;



/**
 * 
 */
public final class JavaConverter {

    /**
     * serialize a Java Object of Type Serializable
     * @param o
     * @return serialized String
     * @throws IOException
     */
    public static String serialize(Object o) throws IOException {
        if(!(o instanceof Serializable))throw new IOException("Java Object is not of type Serializable");
        return serialize((Serializable)o);
    }
    /**
     * serialize a Java Object of Type Serializable
     * @param o
     * @return serialized String
     * @throws IOException
     */
    public static String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serialize(o, baos);
        return Base64Coder.encode(baos.toByteArray(),"UTF-8");
    }

    public static void serialize(Serializable o, railo.commons.io.res.Resource out) throws IOException {
        serialize(o,out.getOutputStream());
    }
    
    public static void serialize(Serializable o, OutputStream os) throws IOException {
        ObjectOutputStream oos=null;
        try {
	        oos = new ObjectOutputStream(os);
	        oos.writeObject(o);
        }
        finally {
           IOUtil.closeEL(oos);
           IOUtil.closeEL(os);
        }
    }
    
    /**
     * unserialize a serialized Object
     * @param str
     * @return unserialized Object
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CoderException 
     */
    public static Object deserialize(String str) throws IOException, ClassNotFoundException, CoderException {
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decode(str,"UTF-8"));
        return deserialize(bais);
    }
    
    public static Object deserialize(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois=null;
        Object o=null;
        try {
	        ois = new ObjectInputStream(is);
	        o=ois.readObject();
        }
        finally {
        	IOUtil.closeEL(ois);
        }
        return o;
    }

    public static Object deserialize(Resource res) throws IOException, ClassNotFoundException {
    	return deserialize(res.getInputStream()); 
    }
    
    
}