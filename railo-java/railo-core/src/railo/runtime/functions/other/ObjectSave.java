package railo.runtime.functions.other;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.output.ByteArrayOutputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.converter.JavaConverter;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class ObjectSave {
    
	public synchronized static Object call(PageContext pc, Object input) throws PageException {
    	return call(pc,input,null);
    }
	
    public synchronized static Object call(PageContext pc, Object input,String filepath) throws PageException {
    	if(!(input instanceof Serializable))
    		throw new ApplicationException("can only serialize object from type Serializable");
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	try {
			JavaConverter.serialize((Serializable)input,baos);
		

	    	byte[] barr = baos.toByteArray();
			
			// store to file
			if(!StringUtil.isEmpty(filepath,true)) {
				Resource res = ResourceUtil.toResourceNotExisting(pc, filepath);
				pc.getConfig().getSecurityManager().checkFileLocation(res);
		        IOUtil.copy(new ByteArrayInputStream(barr),res,true);
			}
	        return barr;
    	
    	}
    	catch (IOException e) {
			throw Caster.toPageException(e);
		}
    }
}
