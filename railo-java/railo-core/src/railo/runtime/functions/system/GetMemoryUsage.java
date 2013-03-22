package railo.runtime.functions.system;

import railo.commons.io.SystemUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;

public final class GetMemoryUsage implements Function {
    
	private static final long serialVersionUID = -7937791531186794443L;

	public static Query call(PageContext pc) throws PageException {
        return call(pc, null);
    }
	public static Query call(PageContext pc,String type) throws PageException {
		if(StringUtil.isEmpty(type))
			return SystemUtil.getMemoryUsageAsQuery(SystemUtil.MEMORY_TYPE_ALL);
		
		type=type.trim().toLowerCase();
		if("heap".equalsIgnoreCase(type))
			return SystemUtil.getMemoryUsageAsQuery(SystemUtil.MEMORY_TYPE_HEAP);
		if("non_heap".equalsIgnoreCase(type) || "nonheap".equalsIgnoreCase(type) || "non-heap".equalsIgnoreCase(type) ||
				"none_heap".equalsIgnoreCase(type) || "noneheap".equalsIgnoreCase(type) || "none-heap".equalsIgnoreCase(type))
				return SystemUtil.getMemoryUsageAsQuery(SystemUtil.MEMORY_TYPE_NON_HEAP);
        
		throw new FunctionException(pc, "GetMemoryUsage", 1, "type", "invalid value ["+type+"], valid values are [heap,non_heap]");
    }
}