package railo.runtime.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.coder.Base64Coder;
import railo.runtime.coder.CoderException;



/**
 * 
 */
public final class JavaConverter extends ConverterSupport implements BinaryConverter {

   
	@Override
	public void writeOut(PageContext pc, Object source, Writer writer) throws ConverterException, IOException {
		if(!(source instanceof Serializable))throw new ConverterException("Java Object is not of type Serializable");
	    writer.write(serialize((Serializable)source));
		writer.flush();
	}

	@Override
	public void writeOut(PageContext pc, Object source, OutputStream os) throws ConverterException, IOException {
		if(!(source instanceof Serializable))throw new ConverterException("Java Object is not of type Serializable");
	    serialize((Serializable)source, os);
	    os.flush();
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
        return Base64Coder.encode(baos.toByteArray());
    }
    public static byte[] serializeAsBinary(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serialize(o, baos);
        return baos.toByteArray();
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
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decode(str));
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