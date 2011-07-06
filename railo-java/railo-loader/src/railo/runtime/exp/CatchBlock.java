package railo.runtime.exp;

import java.io.Serializable;

import railo.runtime.PageContext;
import railo.runtime.op.Castable;
import railo.runtime.type.Struct;

public interface CatchBlock extends Struct,Castable,Serializable {

	/**
	 * FUTURE add to interface
	 * @return the pe
	 */
	public PageException getPageException();

	public void print(PageContext pc);

}
