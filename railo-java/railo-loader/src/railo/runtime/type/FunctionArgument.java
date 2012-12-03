package railo.runtime.type;

import java.io.Serializable;

/**
 * a function argument definition
 */
public interface FunctionArgument extends Serializable {

    public static final int DEFAULT_TYPE_NULL = 0;
    public static final int DEFAULT_TYPE_LITERAL = 1;
    public static final int DEFAULT_TYPE_RUNTIME_EXPRESSION = 2;
	
    /**
     * @return Returns the name of the argument.
     */
    public abstract Collection.Key getName();

    /**
     * @return Returns if argument is required or not.
     */
    public abstract boolean isRequired();

    /**
     * @return Returns the type of the argument.
     */
    public abstract short getType();

    /**
     * @return Returns the type of the argument.
     */
    public abstract String getTypeAsString();

    /**
     * @return Returns the Hint of the argument.
     */
    public abstract String getHint();
 
	/**
     * @return Returns the Display name of the argument.
     */
    public abstract String getDisplayName();
    
    /**
     * @return the default type of the argument
     */
    public int getDefaultType();
    

	/**
	 * @return the meta data defined 
	 */
	public Struct getMetaData();

	public boolean isPassByReference();
}