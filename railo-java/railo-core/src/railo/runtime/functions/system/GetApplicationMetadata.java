package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.type.Struct;

public class GetApplicationMetadata {
	public static Struct call(PageContext pc) {
		return GetApplicationSettings.call(pc, true);
	}
}
