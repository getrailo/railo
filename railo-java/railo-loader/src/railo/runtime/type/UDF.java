package railo.runtime.type;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.Member;
import railo.runtime.dump.Dumpable;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * a user defined function
 * 
 */
public interface UDF extends Function,Dumpable,Member,Cloneable {

	public static final int RETURN_FORMAT_WDDX=0;
	public static final int RETURN_FORMAT_JSON=1;
	public static final int RETURN_FORMAT_PLAIN=2;
	public static final int RETURN_FORMAT_SERIALIZE=3; // FUTURE change to RETURN_FORMAT_CFML
	public static final int RETURN_FORMAT_XML=4;
	

    /**
     * abstract method for the function Body
     * @param pageContext
     * @throws Throwable
     */
    public abstract Object implementation(PageContext pageContext)
            throws Throwable;

    /**
     * return all function arguments of this UDF
     * @return the arguments.
     * @throws PageException
     */
    public abstract FunctionArgument[] getFunctionArguments();

    /**
     * @param pc
     * @param index
     * @return default value
     * @throws PageException
     */
    public abstract Object getDefaultValue(PageContext pc, int index) throws PageException;
    

    // FUTURE public abstract Object getDefaultValue(PageContext pc, int index, Object defaultValue);

    /**
     * @return Returns the functionName.
     */
    public abstract String getFunctionName();

    /**
     * @return Returns the output.
     */
    public abstract boolean getOutput();

    /**
     * @return Returns the returnType.
     */
    public abstract int getReturnType();

    public abstract int getReturnFormat();

    /**
     * returns null when not defined
     * @return value of attribute securejson
     */
    public abstract Boolean getSecureJson();

    /**
     * returns null when not defined
     * @return value of attribute verifyclient
     */
    public abstract Boolean getVerifyClient();
    
    /**
     * @return Returns the returnType.
     */
    public abstract String getReturnTypeAsString();

    public abstract String getDescription();
    
    /**
     * call user defined Funcion with a hashmap of named values
     * @param pageContext
     * @param values named values
     * @param doIncludePath 
     * @return return value of the function
     * @throws PageException
     */
    public abstract Object callWithNamedValues(PageContext pageContext, Struct values, boolean doIncludePath) throws PageException;

    /**
     * call user defined Funcion with parameters as Object Array
     * @param pageContext
     * @param args parameters for the function
     * @param doIncludePath 
     * @return return value of the function
     * @throws PageException
     */
    public abstract Object call(PageContext pageContext, Object[] args,boolean doIncludePath) throws PageException;

    /**
     * @return Returns the displayName.
     */
    public abstract String getDisplayName();

    /**
     * @return Returns the hint.
     */
    public abstract String getHint();


    public abstract PageSource getPageSource();
    
	public abstract Struct getMetaData(PageContext pc) throws PageException; 
	

	public UDF duplicate();
	
	/**
	 * it is the component in whitch this udf is constructed, must not be the same as active udf
	 * @return owner component
	 * @deprecated 
	 */
	public Component getOwnerComponent();

	
}