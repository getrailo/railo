package railo.transformer.expression.literal;

import railo.transformer.expression.Expression;


/**
 * Literal
 */
public interface Literal extends Expression {
    
    
    /**
     * @param defaultValue 
     * @return return value as String (CFML Rules)
     */
    public String getString();
    
    /**
     * @param defaultValue 
     * @return return value as Double Object
     */
    public Double getDouble(Double defaultValue);

    
    /**
     * @param defaultValue 
     * @return return value as a Boolean Object
     */
    public Boolean getBoolean(Boolean defaultValue);

}
