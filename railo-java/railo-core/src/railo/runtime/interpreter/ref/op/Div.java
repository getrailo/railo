package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;

/**
 * Plus operation
 */
public final class Div extends RefSupport implements Ref {

    private Ref right;
    private Ref left;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public Div(Ref left, Ref right) {
        this.left=left;
        this.right=right;
    }

    @Override
	public Object getValue(PageContext pc) throws PageException {
    	double r=Caster.toDoubleValue(right.getValue(pc));
    	if(r==0d)throw new ArithmeticException("Division by zero is not possible");
        return new Double(Caster.toDoubleValue(left.getValue(pc))/r);
    }

    @Override
    public String getTypeName() {
        return "operation";
    }

}
