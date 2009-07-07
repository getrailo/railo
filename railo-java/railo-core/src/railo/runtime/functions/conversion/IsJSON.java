package railo.runtime.functions.conversion;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.JSONExpressionInterpreter;
import railo.runtime.op.Caster;

public class IsJSON {
	public static boolean call(PageContext pc, Object obj) {
		String str=Caster.toString(obj,null);
		if(StringUtil.isEmpty(str,true)) return false;
		try {
			new JSONExpressionInterpreter().interpret(pc, str);
			return true;
		} catch (PageException e) {
			return false;
		}
	}
	
}