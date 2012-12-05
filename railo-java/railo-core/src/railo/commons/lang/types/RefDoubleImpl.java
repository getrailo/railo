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
    
    @Override
    public void setValue(double value) {
        this.value = value;
    }
    
    @Override
    public void plus(double value) {
        this.value+=value;
    }
    
    @Override
    public void minus(double value) {
        this.value-=value;
    }

    @Override
    public Double toDouble() {
        return new Double(value);
    }
    
	@Override
	public double toDoubleValue() {
		return value;
	}
	
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}