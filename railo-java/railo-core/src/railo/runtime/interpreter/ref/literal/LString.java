package railo.runtime.interpreter.ref.literal;

import railo.runtime.PageContext;
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
	

	@Override
	public Object getValue(PageContext pc) {
		return str;
	}	

	@Override
    public String toString() {
		return str;
	}

	@Override
    public String getTypeName() {
		return "literal";
	}

	@Override
    public String getString(PageContext pc) {
        return toString();
    }

	@Override
    public boolean eeq(PageContext pc,Ref other) throws PageException {
		return RefUtil.eeq(pc,this,other);
	}
}
