package railo.runtime.reflection.pairs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * class holds a Constructor and the parameter to call it
 */
public final class ConstructorInstance {

	private Constructor constructor;
	private Object[] args;

	/**
	 * constructor of the class
	 * @param constructor
	 * @param args
	 */
	public ConstructorInstance(Constructor constructor, Object[] args) {
		this.constructor=constructor;
		this.args=args;
	}
	
	/**
	 * Invokes the method
	 * @return return value of the Method
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 */
	public Object invoke() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return constructor.newInstance(args);
	}
	
    /**
     * @return Returns the args.
     */
    public Object[] getArgs() {
        return args;
    }
    /**
     * @return Returns the constructor.
     */
    public Constructor getConstructor() {
        return constructor;
    }
}