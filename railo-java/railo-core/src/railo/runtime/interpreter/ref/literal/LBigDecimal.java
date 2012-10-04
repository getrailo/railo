package railo.runtime.interpreter.ref.literal;

import java.math.BigDecimal;

import railo.runtime.PageContext;
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
    
    @Override
	public Object getValue(PageContext pc) {
        return literal;
    }
    
    @Override
    public Object getCollection(PageContext pc) {
        return getValue(pc);
    }

    @Override
    public String getTypeName() {
        return "number";
    }
    
    @Override
    public Object touchValue(PageContext pc) {
        return getValue(pc);
    }

    @Override
    public String toString() {
        return literal.toString();
    }
    
    @Override
	public boolean eeq(PageContext pc,Ref other) throws PageException {
		return RefUtil.eeq(pc,this,other);
	}
}
