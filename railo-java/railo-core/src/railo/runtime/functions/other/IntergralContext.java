/**
 * Implements the Cold Fusion Function gettickcount
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.UndefinedImpl;

public final class IntergralContext implements Function {
	public static Struct call(PageContext pc) throws PageException {
		Struct sct=new StructImpl();
		sct.setEL("scopeNames", Caster.toArray(((UndefinedImpl)pc.undefinedScope()).getScopeNames()));
		//sct.setEL("stack", FDThreadImpl.getStack((PageContextImpl)pc));
		
		return sct;
	}
}