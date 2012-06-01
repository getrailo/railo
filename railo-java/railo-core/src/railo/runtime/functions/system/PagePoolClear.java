/**
 * Implements the CFML Function gettemplatepath
 */
package railo.runtime.functions.system;

import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.ext.function.Function;

public final class PagePoolClear implements Function {
	
	public static boolean call(PageContext pc) {
		ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();
		clear(config.getMappings());
		clear(config.getCustomTagMappings());
		clear(pc.getApplicationContext().getMappings());
		clear(config.getComponentMappings());
		clear(config.getFunctionMapping());
		clear(config.getServerFunctionMapping());
		clear(config.getTagMapping());
		clear(config.getServerTagMapping());
    	
		return true;
	}
	public static void clear(Mapping[] mappings) {
		if(mappings==null)return;
		for(int i=0;i<mappings.length;i++)	{
			clear(mappings[i]);
		}	
	}
	public static void clear(Mapping mapping) {
		if(mapping==null)return;
		((MappingImpl) mapping).getPageSourcePool().clearPages(null);
	}
}