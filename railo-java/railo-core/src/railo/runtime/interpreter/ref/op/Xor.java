package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;

/**
 * eqv operation
 */
public final class Xor extends RefSupport implements Ref {

    private Ref right;
    private Ref left;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public Xor(Ref left, Ref right) {
        this.left=left;
        this.right=right;
    }

    @Override
	public Object getValue(PageContext pc) throws PageException {
        return Caster.toBooleanValue(left.getValue(pc)) ^ Caster.toBooleanValue(right.getValue(pc))?Boolean.TRUE:Boolean.FALSE;
    }

    @Override
    public String getTypeName() {
        return "operation";
    }
}
