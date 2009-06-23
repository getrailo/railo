

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
	private PageContext pc;

    /**
     * constructor of the class
     * @param left
     * @param right
     */
    public Concat(PageContext pc,Ref left, Ref right) {
    	this.pc=pc;
        this.left=left;
        this.right=right;
    }
    
    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        return Caster.toString(left.getValue())+Caster.toString(right.getValue());
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "operation";
    }
}
