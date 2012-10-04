package railo.runtime.interpreter.ref;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.util.RefUtil;

/**
 * Support class to implement the refs
 */
public abstract class RefSupport implements Ref {
	
	@Override
    public Object getCollection(PageContext pc) throws PageException {
        return getValue(pc);
    }

	@Override
    public Object touchValue(PageContext pc) throws PageException {
        return getValue(pc);
    }


	@Override
	public boolean eeq(PageContext pc, Ref other) throws PageException {
		return RefUtil.eeq(pc,this,other);
	}
}
