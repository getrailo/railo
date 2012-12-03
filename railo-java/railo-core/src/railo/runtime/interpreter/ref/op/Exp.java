package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Operator;

/**
 * imp operation
 */
public final class Exp extends RefSupport implements Ref {

    private Ref right;
    private Ref left;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public Exp(Ref left, Ref right) {
        this.left=left;
        this.right=right;
    }

    @Override
	public Object getValue(PageContext pc) throws PageException {
        return new Double(Operator.exponent(left.getValue(pc),right.getValue(pc)));
    }

    @Override
    public String getTypeName() {
        return "operation";
    }
}
