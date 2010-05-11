/**
 * Implements the Cold Fusion Function gettemplatepath
 */
package railo.runtime.functions.system;

import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.ext.function.Function;

public final class PagePoolClear implements Function {
	
	public static boolean call(PageContext pc) {
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		clear(config.getMappings());
		clear(config.getCustomTagMappings());
		clear(pc.getApplicationContext().getMappings());
		clear(config.getComponentMapping());
		clear(config.getFunctionMapping());
		clear(config.getTagMapping());
		if(config instanceof ConfigWebImpl)clear(((ConfigWebImpl)config).getServerTagMapping());
    	
		return true;
	}
	private static void clear(Mapping[] mappings) {
		if(mappings==null)return;
		for(int i=0;i<mappings.length;i++)	{
			clear(mappings[i]);
		}	
	}
	private static void clear(Mapping mapping) {
		if(mapping==null)return;
		((MappingImpl) mapping).getPageSourcePool().clearPages();
	}
}