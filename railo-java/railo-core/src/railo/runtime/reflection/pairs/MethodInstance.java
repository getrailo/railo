package railo.runtime.reflection.pairs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * class holds a Method and the parameter to call it
 */
public final class MethodInstance {

	private Method method;
	private Object[] args;

	/**
	 * constructor of the class
	 * @param method
	 * @param args
	 */
	public MethodInstance(Method method, Object[] args) {
		this.method=method;
		this.args=args;
		method.setAccessible(true);
	}
	
	/**
	 * Invokes the method
	 * @param o Object to invoke Method on it
	 * @return return value of the Method
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InvocationTargetException
	 */
	public Object invoke(Object o) throws IllegalAccessException, InvocationTargetException {	
		return method.invoke(o,args);
	}
	
    /**
     * @return Returns the args.
     */
    public Object[] getArgs() {
        return args;
    }
    /**
     * @return Returns the method.
     */
    public Method getMethod() {
        return method;
    }
}