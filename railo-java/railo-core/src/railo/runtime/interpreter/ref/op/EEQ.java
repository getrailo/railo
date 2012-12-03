package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;

/**
 * imp operation
 */
public final class EEQ extends RefSupport implements Ref {

    private Ref right;
    private Ref left;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public EEQ(Ref left, Ref right) {
        this.left=left;
        this.right=right;
    }

    @Override
	public Object getValue(PageContext pc) throws PageException {
    	return left.eeq(pc,right)?Boolean.TRUE:Boolean.FALSE;
        //return (left.getValue()==right.getValue())?Boolean.TRUE:Boolean.FALSE;
    }

    @Override
    public String getTypeName() {
        return "operation";
    }
}
