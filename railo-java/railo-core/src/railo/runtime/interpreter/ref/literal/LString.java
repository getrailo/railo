

package railo.runtime.interpreter.ref.literal;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.util.RefUtil;



/**
 * Literal String
 *
 */
public final class LString extends RefSupport implements Literal {
	
	
	private String str;

    /**
	 * constructor of the class
     * @param str 
	 */
	public LString(String str) {
        this.str=str;
	}
	

    /**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() {
		return str;
	}	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getString();
	}

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getTypeName()
	 */
	public String getTypeName() {
		return "literal";
	}

    /**
     * @see railo.runtime.interpreter.ref.literal.Literal#getString()
     */
    public String getString() {
        return str;
    }

	/**
	 * @see railo.runtime.interpreter.ref.Ref#eeq(railo.runtime.interpreter.ref.Ref)
	 */
	public boolean eeq(Ref other) throws PageException {
		return RefUtil.eeq(this,other);
	}
}
