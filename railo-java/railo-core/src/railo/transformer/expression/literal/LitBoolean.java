package railo.transformer.expression.literal;

import railo.transformer.expression.ExprBoolean;

/**
 * Literal Boolean
 */
public interface LitBoolean extends Literal,ExprBoolean {

	//public static final LitBoolean TRUE=new LitBoolean(true,null,null);
	//public static final LitBoolean FALSE=new LitBoolean(false,null,null);
	
	/**
     * @return return value as a boolean value
     */
    public boolean getBooleanValue();

}
