

package railo.runtime.interpreter.ref.op;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.op.Caster;


/**
 * Plus operation
 */
public final class Or extends RefSupport implements Ref {

	private Ref right;
	private Ref left;

	/**
	 * constructor of the class
	 * @param left
	 * @param right
	 */
	public Or(Ref left, Ref right) {
		this.left=left;
		this.right=right;
	}

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() throws PageException {
		return (Caster.toBooleanValue(left.getValue()) || Caster.toBooleanValue(right.getValue()))?Boolean.TRUE:Boolean.FALSE;
	}

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getTypeName()
	 */
	public String getTypeName() {
		return "operation";
	}

}
