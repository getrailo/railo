package railo.commons.lang.types;

/**
 * Integer Type that can be modified
 */
public interface RefDouble {

    /**
     * @param value
     */
    public void setValue(double value);
    
    /**
     * operation plus
     * @param value
     */
    public void plus(double value);
    
    /**
     * operation minus
     * @param value
     */
    public void minus(double value);

    /**
     * @return returns value as Double Object
     */
    public Double toDouble();
    
    /**
     * @return returns value as double
     */
    public double toDoubleValue();
}