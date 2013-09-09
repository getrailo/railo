package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.UDF;

/**
 * returns the root of this actuell Page Context
 */
public final class GetFunctionCalledName implements Function {

	private static final long serialVersionUID = -3345605395096765821L;

	public static String call(PageContext pc) {
		PageContextImpl pci=(PageContextImpl) pc;
		Key name = pci.getActiveUDFCalledName();
		if(name!=null) return name.getString();
		
		UDF[] udfs = ((PageContextImpl)pc).getUDFs();
		if(udfs.length==0) return "";
		return udfs[udfs.length-1].getFunctionName();
	}
}