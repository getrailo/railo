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
    
    /**
     * @see railo.commons.lang.types.RefLong#setValue(long)
     */
    public void setValue(long value) {
        this.value = value;
    }
    
    /**
     * @see railo.commons.lang.types.RefLong#plus(long)
     */
    public void plus(long value) {
        this.value+=value;
    }
    
    /**
     * @see railo.commons.lang.types.RefLong#minus(long)
     */
    public void minus(long value) {
        this.value-=value;
    }

    /**
     * @see railo.commons.lang.types.RefLong#toLong()
     */
    public Long toLong() {
        return new Long(value);
    }
    
	/**
	 * @see railo.commons.lang.types.RefLong#toLongValue()
	 */
	public long toLongValue() {
		return value;
	}
	
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.valueOf(value);
    }
}