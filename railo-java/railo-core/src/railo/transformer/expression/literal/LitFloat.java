package railo.transformer.expression.literal;
import railo.transformer.expression.ExprFloat;

/**
 * Literal Double Value
 */
public interface LitFloat extends Literal,ExprFloat {


	/**
     * @return return value as double value
     */ 
    public float getFloatValue();
    
    public Float getFloat();
}
