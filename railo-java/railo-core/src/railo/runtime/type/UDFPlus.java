package railo.runtime.type;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface UDFPlus extends UDF {
	 public Object getDefaultValue(PageContext pc, int index, Object defaultValue) throws PageException;
	 public int getIndex();
}
