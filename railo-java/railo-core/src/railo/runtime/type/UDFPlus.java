package railo.runtime.type;

import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

// FUTURE add to interface UDF

public interface UDFPlus extends UDF {
	

	public static final int RETURN_FORMAT_JAVA=5;
	

    /**
     * call user defined Funcion with a struct
     * @param pageContext
     * @param values named values
     * @param doIncludePath 
     * @return return value of the function
     * @throws PageException
     */
    public abstract Object callWithNamedValues(PageContext pageContext,Collection.Key calledName,Struct values, boolean doIncludePath) throws PageException;

    /**
     * call user defined Funcion with parameters as Object Array
     * @param pageContext
     * @param args parameters for the function
     * @param doIncludePath 
     * @return return value of the function
     * @throws PageException
     */
    public abstract Object call(PageContext pageContext, Collection.Key calledName, Object[] args, boolean doIncludePath) throws PageException;
	
	 public Object getDefaultValue(PageContext pc, int index, Object defaultValue) throws PageException;
	 public int getIndex();
	 
	 
	 // !!!!!! do not move to public interface, make for example a interface calle UDFMod
	 public void setOwnerComponent(ComponentImpl component);
	 public void setAccess(int access);
	 
	 public abstract int getReturnFormat(int defaultFormat);
	    
}
