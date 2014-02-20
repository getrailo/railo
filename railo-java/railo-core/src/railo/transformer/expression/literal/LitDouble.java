package railo.transformer.expression.literal;

import railo.transformer.expression.ExprDouble;


/**
 * Literal Double Value
 */
public interface LitDouble extends Literal,ExprDouble {

	//private static final Type CONSTANTS_DOUBLE = Type.getType(ConstantsDouble.class);
	//public static final LitDouble ZERO=new LitDouble(0,null,null);
	
	/**
     * @return return value as double value
     */ 
    public double getDoubleValue();
}
