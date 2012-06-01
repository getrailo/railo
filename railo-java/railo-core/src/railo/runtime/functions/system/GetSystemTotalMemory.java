package railo.runtime.functions.system;

import railo.commons.io.SystemUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public class GetSystemTotalMemory implements Function {
	
	private static final long serialVersionUID = 6459096452887146460L;

	public static double call(PageContext pc) {
		return Caster.toDoubleValue(SystemUtil.getTotalBytes());
	}
}
