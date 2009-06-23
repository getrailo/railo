

package railo.runtime.interpreter.ref;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.util.RefUtil;

/**
 * Support class to implement the refs
 */
public abstract class RefSupport implements Ref {

    
    /**
     * @see railo.runtime.interpreter.ref.Ref#getCollection()
     */
    public Object getCollection() throws PageException {
        return getValue();
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#touchValue()
     */
    public Object touchValue() throws PageException {
        return getValue();
    }


	/**
	 * @see railo.runtime.interpreter.ref.Ref#eeq(railo.runtime.interpreter.ref.Ref)
	 */
	public boolean eeq(Ref other) throws PageException {
		return RefUtil.eeq(this,other);
	}
}
