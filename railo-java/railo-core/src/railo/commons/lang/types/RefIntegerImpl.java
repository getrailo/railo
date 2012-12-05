package railo.commons.lang.types;

/**
 * Integer Type that can be modified
 */
public class RefIntegerImpl implements RefInteger {

    private int value;

    /**
     * @param value
     */
    public RefIntegerImpl(int value) {
        this.value=value;
    }
    public RefIntegerImpl() {
    }
    
    /**
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
    }
    
    /**
     * operation plus
     * @param value
     */
    public void plus(int value) {
        this.value+=value;
    }
    
    /**
     * operation minus
     * @param value
     */
    public void minus(int value) {
        this.value-=value;
    }

    /**
     * @return returns value as integer
     */
    public Integer toInteger() {
        return Integer.valueOf(value);
    }
    /**
     * @return returns value as integer
     */
    public Double toDouble() {
        return new Double(value);
    }
    

	@Override
	public double toDoubleValue() {
		return value;
	}
	
	@Override
	public int toInt() {
		return value;
	}
    
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}