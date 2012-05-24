package railo.runtime.interpreter.ref.literal;

import railo.runtime.PageContext;
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
    public String getString(PageContext pc) {
        return toString();
    }

    @Override
    public String toString() {
        return Caster.toString(literal.doubleValue());
    }

    @Override
	public boolean eeq(PageContext pc,Ref other) throws PageException {
		if(other instanceof LNumber){
			return literal.doubleValue()==((LNumber)other).literal.doubleValue();
		}
		// TODO Auto-generated method stub
		return RefUtil.eeq(pc,this,other);
	}
}
