package railo.runtime.functions.system;

import railo.commons.io.SystemUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;

public class Sleep {

	public static String call(PageContext pc, double duration) throws FunctionException {
		if(duration>=0) {
			SystemUtil.sleep((long)duration);
		}
		else throw new FunctionException(pc,"sleep",1,"duration","attribute interval must be greater or equal to 0, now ["+(duration)+"]");
		return null;
		
	}
}
