package railo.runtime.type.scope;

import java.util.Set;

import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
// FUTURE add to Argument Interface
public interface ArgumentPro extends Argument,BindScope {
	
	public Object setArgument(Object obj) throws PageException;

	public static final Object NULL = null;
	
	public Object getFunctionArgument(String key, Object defaultValue);

	public Object getFunctionArgument(Collection.Key key, Object defaultValue);
	
	public void setFunctionArgumentNames(Set functionArgumentNames);

	public boolean containsFunctionArgumentKey(Key key);
	
}
