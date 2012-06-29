package railo.runtime.functions.system;

import railo.commons.io.SystemUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public class GetCPUUsage implements Function {

	private static final long serialVersionUID = 2264215038554428321L;
	
	public static double call(PageContext pc) throws ApplicationException {
		return call(pc, 1000);
	}
	public static double call(PageContext pc, double time) throws ApplicationException {
		return Caster.toDoubleValue(SystemUtil.getCpuUsage((long)time));
	}

}
