package railo.runtime.net.amf;

import java.util.Map;

import railo.runtime.exp.PageException;

/**
 * Cast a CFML object to AMF Objects and the other way
 */
public interface AMFCaster {
    
	public void init(Map arguments);
	
    /**
     * cast cfml Object to AMF Object
     * @param o
     * @return
     * @throws PageException
     */
    public Object toAMFObject(Object o) throws PageException;
    
    /**
     * cast a amf Object to cfml Object
     * @param amf
     * @return
     * @throws PageException
     */
    public Object toCFMLObject(Object amf) throws PageException;

}