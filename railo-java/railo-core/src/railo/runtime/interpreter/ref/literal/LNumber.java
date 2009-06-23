

package railo.runtime.interpreter.ref.literal;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.util.RefUtil;
import railo.runtime.op.Caster;

/**
 * Literal Number
 */
public final class LNumber implements Literal {

    public static final LNumber ZERO = new LNumber(new Double(0));
    public static final LNumber ONE = new LNumber(new Double(1));
    
    
    
	private Double literal;

    /**
     * constructor of the class
     * @param literal
     */
    public LNumber(Double literal) {
        this.literal=literal;
    }

    /**
     * constructor of the class
     * @param literal
     * @throws PageException 
     */
    public LNumber(String literal) throws PageException {
        this.literal=Caster.toDouble(literal);
    }
    
    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() {
        return literal;
    }
    
    /**
     * @see railo.runtime.interpreter.ref.Ref#getCollection()
     */
    public Object getCollection() {
        return getValue();
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "number";
    }
    

    /**
     * @see railo.runtime.interpreter.ref.Ref#touchValue()
     */
    public Object touchValue() {
        return getValue();
    }

    /**
     * @see railo.runtime.interpreter.ref.literal.Literal#getString()
     */
    public String getString() {
        return Caster.toString(literal.doubleValue());
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
			return literal.doubleValue()==((LNumber)other).literal.doubleValue();
		}
		// TODO Auto-generated method stub
		return RefUtil.eeq(this,other);
	}
}
