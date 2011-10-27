package railo.commons.lang.types;

/**
 * Integer Type that can be modified
 */
public final class RefIntegerSync extends RefIntegerImpl {

    /**
     * @param value
     */
    public RefIntegerSync(int value) {
        super(value);
    }
    
    /**
     * @param value
     */
    public synchronized void setValue(int value) {
    	super.setValue(value);
    }
    
    /**
     * operation plus
     * @param value
     */
    public synchronized void plus(int value) {
    	super.plus(value);
    }
    
    /**
     * operation minus
     * @param value
     */
    public synchronized void minus(int value) {
        super.minus(value);
    }
}