package railo.runtime.type.scope;


public interface Variables extends Scope {

    /** 
     * sets if scope is binded to a closure
     * @param bind 
     */
    public void setBind(boolean bind);
    

    /** 
     * @return returns if scope is binded to a closure 
     */
    public abstract boolean isBind();
    
	//public void registerUDF(Collection.Key key, UserDefinedFunction udf);
	//public void registerUDF(String key, UserDefinedFunction udf);
}
