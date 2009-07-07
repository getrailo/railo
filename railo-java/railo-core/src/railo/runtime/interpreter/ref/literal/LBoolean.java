package railo.runtime.interpreter.ref.literal;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.util.RefUtil;
import railo.runtime.op.Caster;

/**
 * constructor of the class
 */
public final class LBoolean extends RefSupport implements Literal {

    /**
     * Field <code>TRUE</code>
     */
    public static final Ref TRUE = new LBoolean(Boolean.TRUE);
    /**
     * Field <code>FALSE</code>
     */
    public static final Ref FALSE = new LBoolean(Boolean.FALSE);
    
    private Boolean literal;

	/**
	 * constructor with Boolean
	 * @param literal
	 */
	public LBoolean(Boolean literal) {
		this.literal=literal;
	}
	
	/**
	 * constructor with boolean
	 * @param b
	 */
	public LBoolean(boolean b) {
		this.literal=b?Boolean.TRUE:Boolean.FALSE;
	}
	
    /**
	 * constructor with boolean
	 * @param str
	 * @throws PageException 
	 */
	public LBoolean(String str) throws PageException {
		this.literal=Caster.toBoolean(str);
	}
	
	
	/**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() {
		return literal;
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
        return Caster.toString(literal.booleanValue());
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getString(); 
    }

	/**
	 * @see railo.runtime.interpreter.ref.Ref#eeq(railo.runtime.interpreter.ref.Ref)
	 */
	public boolean eeq(Ref other) throws PageException {
		if(other instanceof LNumber){
			return literal.booleanValue()==((LBoolean)other).literal.booleanValue();
		}
		return RefUtil.eeq(this,other);
	}

}
