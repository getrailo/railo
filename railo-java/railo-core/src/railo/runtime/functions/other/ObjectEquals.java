package railo.runtime.functions.other;


import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Operator;

public class ObjectEquals {
	public static boolean call(PageContext pc , Object left, Object right) throws PageException {
		// null
		if(left==null){
			return right==null;
		}
		return Operator.equals(left, right, true, true);
	}
}
