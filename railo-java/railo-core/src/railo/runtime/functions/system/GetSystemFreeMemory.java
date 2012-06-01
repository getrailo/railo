package railo.runtime.functions.system;

import railo.commons.io.SystemUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public class GetSystemFreeMemory implements Function {
	
	private static final long serialVersionUID = -2808311251929634506L;

	public static double call(PageContext pc) throws ApplicationException {
		return Caster.toDoubleValue(SystemUtil.getFreeBytes());
	}

}
