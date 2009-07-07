package railo.runtime.db;

/**
 * a ZQL Function
 */
public interface ZQLFunction {
    
    /**
     * method to call the funtion
     * @param arguments arguments to call the funtion
     * @return result of the function
     */
    public Object call(Object[] arguments);
}