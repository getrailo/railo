package railo.runtime.interpreter.ref.var;

import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.Set;

public final class Assign extends RefSupport implements Ref {
	
	private Ref value;
	private Set coll;


    public Assign(Ref coll, Ref value) throws ExpressionException {
        if(!(coll instanceof Set))
            throw new ExpressionException("can't assign value to object of type "+coll.getTypeName());
        this.coll=(Set) coll;
        this.value=value;
    }

    public Assign(Set coll, Ref value) {
        this.coll=coll;
        this.value=value;
    }
	
	/**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() throws PageException {
        return coll.setValue(value.getValue());
	}

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getTypeName()
	 */
	public String getTypeName() {
		return "operation";
	}
}
