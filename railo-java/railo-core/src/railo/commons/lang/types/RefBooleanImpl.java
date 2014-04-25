package railo.commons.lang.types;

/**
 * Integer Type that can be modified
 */
public final class RefBooleanImpl implements RefBoolean {//MUST add interface Castable

    private boolean value;


    public RefBooleanImpl() {}
    
    /**
     * @param value
     */
    public RefBooleanImpl(boolean value) {
        this.value=value;
    }
    
    /**
     * @param value
     */
    @Override
	public void setValue(boolean value) {
        this.value = value;
    }
    
    /**
     * @return returns value as Boolean Object
     */
    @Override
	public Boolean toBoolean() {
        return value?Boolean.TRUE:Boolean.FALSE;
    }
    
    /**
     * @return returns value as boolean value
     */
    @Override
	public boolean toBooleanValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value?"true":"false";
    }
}