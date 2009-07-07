package railo.runtime.reflection.pairs;

import java.lang.reflect.Method;


/**
 * Hold a pair of method and parameter to invoke
 */
public final class MethodParameterPair {
	
	private Method method;
	private Object[] parameters;
	
	/**
	 * constructor of the pair Object
	 * @param method
	 * @param parameters
	 */
	public MethodParameterPair(Method method, Object[] parameters) {
		this.method=method;
		this.parameters=parameters;
		method.setAccessible(true);
	}
	
	/**
	 * returns the Method
	 * @return returns the Method
	 */
	public Method getMethod() {
		return method;
	}
	
	/**
	 * returns the Parameters
	 * @return returns the Parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}
	
}