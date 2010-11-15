package railo.runtime.cache.eh.remote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import railo.loader.util.Util;

public class Converter {
	public static Object toObject(String contentType,InputStream is) throws IOException, ClassNotFoundException {
		try	{
	    	if("application/x-java-serialized-object".equals(contentType)){
	    		ObjectInputStream ois=new ObjectInputStream(is);
	    		return ois.readObject();
		    }
		    // other
		    return Util.toString(is);
		}
    	finally	{
    		Util.closeEL(is);
    	}
	}
	
	public static byte[] toBytes(Object value) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(); // returns
	    ObjectOutputStream oos = new ObjectOutputStream(os);
	    oos.writeObject(value);
	    oos.flush();
	    return os.toByteArray();
	}
}
