package railo.commons.io.res.util;

import java.io.File;

import railo.commons.io.res.Resource;
import railo.commons.lang.CFTypes;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.UDF;

public class UDFFilter implements ResourceAndResourceNameFilter {

	private UDF udf;
	private Object[] args=new Object[1];
	
	public UDFFilter(UDF udf) throws ExpressionException{
		this.udf=udf;
		
		// check UDF return type
		int type = udf.getReturnType();
		if(type!=CFTypes.TYPE_BOOLEAN && type!=CFTypes.TYPE_ANY)
			throw new ExpressionException("invalid return type ["+udf.getReturnTypeAsString()+"] for UDF Filter, valid return types are [boolean,any]");
		
		// check UDF arguments
		FunctionArgument[] args = udf.getFunctionArguments();
		if(args.length>1)
			throw new ExpressionException("UDF filter has to many arguments ["+args.length+"], should have at maximum 1 argument");
		
		if(args.length==1){
			type=args[0].getType();
			if(type!=CFTypes.TYPE_STRING && type!=CFTypes.TYPE_ANY)
				throw new ExpressionException("invalid type ["+args[0].getTypeAsString()+"] for first argument of UDF Filter, valid return types are [string,any]");
		}
		
		
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
