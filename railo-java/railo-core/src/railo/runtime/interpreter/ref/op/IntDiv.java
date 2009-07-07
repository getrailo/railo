package railo.runtime.interpreter.ref.op;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;

/**
 * Plus operation
 */
public final class IntDiv extends RefSupport implements Ref {

    private Ref right;
    private Ref left;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public IntDiv(Ref left, Ref right) {
        this.left=left;
        this.right=right;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        return new Double(Caster.toIntValue(left.getValue())/Caster.toIntValue(right.getValue()));
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "operation";
    }

}
