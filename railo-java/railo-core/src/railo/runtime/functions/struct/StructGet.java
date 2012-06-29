/**
 * Implements the CFML Function structget
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class StructGet implements Function {
	public static Object call(PageContext pc , String string) throws PageException {
		try {
			Object obj = pc.getVariable(string);
			if(obj instanceof Struct)
				return obj;
		} 
		catch (PageException e) {
		}
		return pc.setVariable(string,new StructImpl());
		
	}
}