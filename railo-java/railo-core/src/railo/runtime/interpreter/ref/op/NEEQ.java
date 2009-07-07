package railo.runtime.interpreter.ref.op;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;

/**
 * imp operation
 */
public final class NEEQ extends RefSupport implements Ref {

    private Ref right;
    private Ref left;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public NEEQ(Ref left, Ref right) {
        this.left=left;
        this.right=right;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
    	return left.eeq(right)?Boolean.FALSE:Boolean.TRUE;
        //return (left.getValue()!=right.getValue())?Boolean.TRUE:Boolean.FALSE;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "operation";
    }
}
