/**
 * Implements the CFML Function gettickcount
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class IntergralContext implements Function {

	private static final long serialVersionUID = -330160528570830717L;

	public static Struct call(PageContext pc) throws PageException {
		Struct sct=new StructImpl();
		sct.setEL(KeyImpl.init("scopeNames"), Caster.toArray(pc.undefinedScope().getScopeNames()));
		//sct.setEL("stack", FDThreadImpl.getStack((PageContextImpl)pc));
		
		return sct;
	}
}