package railo.runtime.component;

import java.io.Serializable;

public interface Member extends Serializable {
    
	/*
	public static final int TYPE_FUNCTION=0;
	public static final int TYPE_DATA=1;
	public int getType()
	
	
	*/
	/**
	 * return the access modifier of this member
	 * @return the access
	 */
	public int getAccess();

	/**
	 * return the value itself
	 * @return value
	 */
	public Object getValue();
}