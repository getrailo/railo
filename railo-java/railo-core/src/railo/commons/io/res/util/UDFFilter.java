package railo.commons.io.res.util;

import java.io.File;

import railo.commons.io.res.Resource;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.UDF;

public class UDFFilter extends UDFFilterSupport implements ResourceAndResourceNameFilter {

	public UDFFilter(UDF udf) throws ExpressionException{
		super(udf);
	}
	
    /**
     * @see railo.commons.io.res.filter.ResourceFilter#accept(railo.commons.io.res.Resource)
     */
    public boolean accept(String path) {
    	args[0]=path;
    	try {
			return Caster.toBooleanValue(udf.call(ThreadLocalPageContext.get(), args, true));
			
		} 
    	catch (PageException e) {
			throw new PageRuntimeException(e);
		}
    }
    
    
    public boolean accept(Resource file) {
    	return accept(file.getAbsolutePath());
    }

	/**
	 * @see railo.commons.io.res.filter.ResourceNameFilter#accept(railo.commons.io.res.Resource, java.lang.String)
	 */
	public boolean accept(Resource parent, String name) {
		String path=parent.getAbsolutePath();
		if(path.endsWith(File.separator)) path+=name;
		else path+=File.separator+name;
		return accept(path);
	}
	
    /**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "UDFFilter:"+udf;
	}
}
