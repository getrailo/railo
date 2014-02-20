package railo.transformer.expression.literal;

/**
 * Literal Double Value
 */
public interface LitLong extends Literal {

	/**
     * @return return value as int
     */ 
    public long getLongValue();
    
    /**
     * @return return value as Double Object
     */
    public Long getLong();
}
