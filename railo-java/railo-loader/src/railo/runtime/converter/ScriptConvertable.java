package railo.runtime.converter;


/**
 * Converter to convert Object to a String
 */
public interface ScriptConvertable {

    /**
     * convert object to String
     * @return serialized Object
     */
    public String serialize();
    
    // FUTURE String serialize(Set<Object> done); 
}