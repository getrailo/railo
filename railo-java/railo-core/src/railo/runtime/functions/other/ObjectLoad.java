package railo.runtime.functions.other;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.converter.JavaConverter;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public class ObjectLoad {
	public synchronized static Object call(PageContext pc, Object input) throws PageException {
    	InputStream is;
    	boolean closeStream=true;
		if(Decision.isBinary(input)) {
    		is=new ByteArrayInputStream(Caster.toBinary(input));
    	}
    	else if(input instanceof InputStream) {
    		is=(InputStream)input;
    		closeStream=false;
    	}
    	else {
    		Resource res = ResourceUtil.toResourceExisting(pc, Caster.toString(input));
    		pc.getConfig().getSecurityManager().checkFileLocation(res);
    		try {
    			is=res.getInputStream();
			} catch (IOException e) {
				throw Caster.toPageException(e);
			}
    	}
		
		try {
			return JavaConverter.deserialize(is);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
		finally{
			if(closeStream)IOUtil.closeEL(is);
		}
    }

}
