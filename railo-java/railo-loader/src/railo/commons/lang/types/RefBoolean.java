package railo.commons.lang.types;

/**
 * Integer Type that can be modified
 */
public interface RefBoolean {
    
    /**
     * @param value
     */
    public void setValue(boolean value);
    
    /**
     * @return returns value as Boolean Object
     */
    public Boolean toBoolean();
    
    /**
     * @return returns value as boolean value
     */
    public boolean toBooleanValue();
    
}