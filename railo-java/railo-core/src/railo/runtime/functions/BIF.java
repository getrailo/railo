package railo.runtime.functions;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public abstract class BIF implements Function {
	public abstract Object invoke(PageContext pc, Object[] args) throws PageException;
}
