/**
 * Implements the Cold Fusion Function gettemplatepath
 */
package railo.runtime.functions.system;

import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class PagePoolClear implements Function {
	
	public static boolean call(PageContext pc) {
		clear(pc.getConfig().getMappings());
		clear(pc.getConfig().getCustomTagMappings());
		clear(pc.getApplicationContext().getMappings());
		return true;
	}

	private static void clear(Mapping[] mappings) {
		if(mappings==null)return;
		MappingImpl mapping;
		for(int i=0;i<mappings.length;i++)	{
			mapping=(MappingImpl) mappings[i];
			mapping.getPageSourcePool().clearPages();
		}	
	}
}