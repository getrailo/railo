package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;

public class CTCacheClear {
	public static String call(PageContext pc) {
		ConfigImpl config = (ConfigImpl)pc.getConfig();
		config.clearCTCache();
		return null;
	}
}
