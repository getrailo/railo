package railo.commons.lang.types;

/**
 * Integer Type that can be modified
 */
public final class RefDoubleImpl implements RefDouble {

    private double value;

    public RefDoubleImpl(double value) {
        this.value=value;
    }
    
    /**
     * Constructor of the class
     */
    public RefDoubleImpl() {
    }
    
    /**
     * @see railo.commons.lang.types.RefDouble#setValue(double)
     */
    public void setValue(double value) {
        this.value = value;
    }
    
    /**
     * @see railo.commons.lang.types.RefDouble#plus(double)
     */
    public void plus(double value) {
        this.value+=value;
    }
    
    /**
     * @see railo.commons.lang.types.RefDouble#minus(double)
     */
    public void minus(double value) {
        this.value-=value;
    }

    /**
     * @see railo.commons.lang.types.RefDouble#toDouble()
     */
    public Double toDouble() {
        return new Double(value);
    }
    
	/**
	 * @see railo.commons.lang.types.RefDouble#toDoubleValue()
	 */
	public double toDoubleValue() {
		return value;
	}
	
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.valueOf(value);
    }
}