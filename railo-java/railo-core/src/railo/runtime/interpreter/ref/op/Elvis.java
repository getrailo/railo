package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.decision.IsDefined;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.literal.LFunctionValue;
import railo.runtime.interpreter.ref.var.Variable;

public class Elvis  extends RefSupport implements Ref{

	private Variable left;
	private Ref right;

	public Elvis(Variable left, Ref right) {
		this.left=left;
		this.right=right; 
	}


    @Override
	public Object getValue(PageContext pc) throws PageException {
    	String[] arr = LFunctionValue.toStringArray(pc, left);
    	return IsDefined.invoke(pc, arr,false)?left.getValue(pc):right.getValue(pc);
    }

    @Override
    public String getTypeName() {
        return "operation";
    }
}
