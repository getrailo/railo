package railo.runtime.interpreter.ref.var;

import railo.runtime.PageContext;
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
        	throw new ExpressionException("invalid assignment left-hand side ("+coll.getTypeName()+")");
        this.coll=(Set) coll;
        this.value=value;
    }

    public Assign(Set coll, Ref value) {
        this.coll=coll;
        this.value=value;
    }
	
    @Override
    public Object getValue(PageContext pc) throws PageException {
        return coll.setValue(pc,value.getValue(pc));
	}

    @Override
    public String getTypeName() {
		return "operation";
	}
}
