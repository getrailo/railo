package railo.runtime;


import railo.runtime.dump.Dumpable;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;


public interface Interface extends Dumpable,CFObject {
	
	public boolean instanceOf(String type);

	public String getCallPath();

	public PageSource getPageSource();

	public Struct getMetaData(PageContext pc) throws PageException;
	

    /* FUTURE also in component
    public void registerUDF(String key, UDF udf);
    
    public void registerUDF(Collection.Key key, UDF udf);
    
    public void registerUDF(String key, UDFProperties props);
    
    public void registerUDF(Collection.Key key, UDFProperties props);
    */
    
    
}
