package railo.runtime;


import railo.runtime.dump.Dumpable;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFProperties;


public interface Interface extends Dumpable,CIObject {
	
	public boolean instanceOf(String type);

	public String getCallPath();

	public PageSource getPageSource();

	public Struct getMetaData(PageContext pc) throws PageException;
	

    public void registerUDF(String key, UDF udf);
    
    public void registerUDF(Collection.Key key, UDF udf);
    
    public void registerUDF(String key, UDFProperties props);
    
    public void registerUDF(Collection.Key key, UDFProperties props);
    
    
    
}
