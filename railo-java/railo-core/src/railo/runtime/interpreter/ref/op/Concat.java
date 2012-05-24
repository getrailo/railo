package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;

/**
 * Concat operation
 */
public final class Concat extends RefSupport implements Ref {
    
    private Ref right;
    private Ref left;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public Concat(Ref left, Ref right) {
    	this.left=left;
        this.right=right;
    }
    
    @Override
    public Object getValue(PageContext pc) throws PageException {
        return Caster.toString(left.getValue(pc))+Caster.toString(right.getValue(pc));
    }

    @Override
    public String getTypeName() {
        return "operation";
    }
}
