package railo.runtime.functions.component;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.type.Struct;

public class ComponentCacheList {
	public static Struct call(PageContext pc) {
		ConfigImpl config = (ConfigImpl)pc.getConfig();
		return config.listComponentCache();
	}
}
