package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.op.Decision;
import railo.runtime.type.ObjectWrap;

public class IsClosure {
	public static boolean call(PageContext pc, Object obj) {
		if(obj instanceof ObjectWrap) {
        	return call(pc,((ObjectWrap)obj).getEmbededObject(null));
        }
		return Decision.isClosure(obj);
	}
}
