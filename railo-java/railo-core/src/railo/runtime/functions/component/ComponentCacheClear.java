package railo.runtime.functions.component;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;

public class ComponentCacheClear {
	public static String call(PageContext pc) {
		ConfigImpl config = (ConfigImpl)pc.getConfig();
		config.clearComponentCache();
		return null;
	}
}
