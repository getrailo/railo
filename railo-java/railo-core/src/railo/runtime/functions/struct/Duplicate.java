/**
 * Implements the CFML Function duplicate
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;

public final class Duplicate extends BIF {
	
	private static final long serialVersionUID = 74899451528656931L;
	
	public static Object call(PageContext pc , Object object) {
		return Duplicator.duplicate(object,true);
	}
	
	public static Object call(PageContext pc , Object object,boolean deepCopy) {
		return Duplicator.duplicate(object,deepCopy);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2) return call(pc,args[0],Caster.toBooleanValue(args[1]));
		return call(pc,args[0]);
	}
	
	
	
}