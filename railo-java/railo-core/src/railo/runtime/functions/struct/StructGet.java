/**
 * Implements the CFML Function structget
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class StructGet extends BIF {

	private static final long serialVersionUID = -4661190117177511485L;

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
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toString(args[0]));
	}
}