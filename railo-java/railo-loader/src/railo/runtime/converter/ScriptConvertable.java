package railo.runtime.converter;

import java.util.Set;


/**
 * Converter to convert Object to a String
 */
public interface ScriptConvertable {

    /**
     * convert object to String
     * @return serialized Object
     * @deprecated use instead <code>serialize(Set<Object> done</code>
     */
    public String serialize();
    
    /**
     * convert object to String
     * @return serialized Object
     */
    public String serialize(Set<Object> done); 
}