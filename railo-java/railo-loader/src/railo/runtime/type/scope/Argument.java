package railo.runtime.type.scope;

import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Scope;

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

}