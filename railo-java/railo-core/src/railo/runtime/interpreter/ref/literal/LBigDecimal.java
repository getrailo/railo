package railo.runtime.interpreter.ref.literal;

import java.math.BigDecimal;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.util.RefUtil;

/**
 * Literal Number
 */
public final class LBigDecimal implements Ref {

    public static final LBigDecimal ZERO = new LBigDecimal(BigDecimal.ZERO);
    public static final LBigDecimal ONE = new LBigDecimal(BigDecimal.ONE);
    
    
    
	private BigDecimal literal;

    /**
     * constructor of the class
     * @param literal
     */
    public LBigDecimal(BigDecimal literal) {
        this.literal=literal;
    }

    /**
     * constructor of the class
     * @param literal
     * @throws PageException 
     */
    public LBigDecimal(String literal) throws PageException {
        this.literal=new BigDecimal(literal);
    }
    
    public BigDecimal getBigDecimal() {
        return literal;
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
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return literal.toString();
    }

	/**
	 * @see railo.runtime.interpreter.ref.Ref#eeq(railo.runtime.interpreter.ref.Ref)
	 */
	public boolean eeq(Ref other) throws PageException {
		return RefUtil.eeq(this,other);
	}
}
