package railo.runtime.type;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface CustomType {

	public Object convert(PageContext pc, Object o) throws PageException;
	public Object convert(PageContext pc, Object o, Object defaultValue);
}
