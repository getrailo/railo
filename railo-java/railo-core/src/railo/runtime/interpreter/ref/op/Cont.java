package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;

/**
 * imp operation
 */
public final class Cont extends RefSupport implements Ref {

    private Ref cont;
    private Ref right;
    private Ref left;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public Cont(Ref cont, Ref left, Ref right) {
    	this.cont=cont;
    	this.left=left;
        this.right=right;
    }

    @Override
	public Object getValue(PageContext pc) throws PageException {
        return Caster.toBooleanValue(cont.getValue(pc))?left.getValue(pc):right.getValue(pc);
    }

    @Override
    public String getTypeName() {
        return "operation";
    }
}
