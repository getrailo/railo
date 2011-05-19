package railo.runtime.type.scope;

import java.util.Set;

import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Scope;
import railo.runtime.type.Collection.Key;

/**
 * interface for Argument scope
 */
public interface Argument extends Scope,Array {

    /** 
     * sets if scope is binded to a other variable for using outside of a udf 
     * @param bind 
     */
    public abstract void setBind(boolean bind);

    /** 
     * @return returns if scope is binded to a other variable for using outside of a udf 
     */
    public abstract boolean isBind();

    /**
     * insert a key in argument scope at defined position
     * @param index
     * @param key
     * @param value
     * @return boolean
     * @throws PageException
     */
    public abstract boolean insert(int index, String key, Object value)
            throws PageException;
    

	public Object setArgument(Object obj) throws PageException;

	public static final Object NULL = null;
	
	public Object getFunctionArgument(String key, Object defaultValue);

	public Object getFunctionArgument(Collection.Key key, Object defaultValue);
	
	public void setFunctionArgumentNames(Set functionArgumentNames);

	public boolean containsFunctionArgumentKey(Key key);
	

}