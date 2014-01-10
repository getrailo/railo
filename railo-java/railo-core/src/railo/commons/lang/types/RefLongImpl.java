package railo.commons.lang.types;

/**
 * Integer Type that can be modified
 */
public final class RefLongImpl implements RefLong {

    private long value;


    /**
     * Constructor of the class
     * @param value
     */
    public RefLongImpl(long value) {
        this.value=value;
    }
    
    /**
     * Constructor of the class
     */
    public RefLongImpl() {
    }
    
    @Override
    public void setValue(long value) {
        this.value = value;
    }
    
    @Override
    public void plus(long value) {
        this.value+=value;
    }
    
    @Override
    public void minus(long value) {
        this.value-=value;
    }

    @Override
    public Long toLong() {
        return Long.valueOf(value);
    }
    
	@Override
	public long toLongValue() {
		return value;
	}
	
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}