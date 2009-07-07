package railo.runtime.reflection.pairs;

import java.lang.reflect.Constructor;


/**
 * Hold a pair of method and parameter to invoke
 */
public final class ConstructorParameterPair {
	
	private Constructor constructor;
	private Object[] parameters;
	
	/**
	 * constructor of the pair Object
	 * @param constructor
	 * @param parameters
	 */
	public ConstructorParameterPair(Constructor constructor, Object[] parameters) {
		this.constructor=constructor;
		this.parameters=parameters;
		constructor.setAccessible(true);
		
	}
	
	/**
	 * returns the Constructor
	 * @return returns the Constructor
	 */
	public Constructor getConstructor() {
		return constructor;
	}
	
	/**
	 * returns the Parameters
	 * @return returns the Parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}
	
}