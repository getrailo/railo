package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.type.Struct;

public class CTCacheList {
	public static Struct call(PageContext pc) {
		ConfigImpl config = (ConfigImpl)pc.getConfig();
		return config.listCTCache();
	}
}
