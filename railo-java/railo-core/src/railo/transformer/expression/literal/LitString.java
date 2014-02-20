package railo.transformer.expression.literal;

import railo.transformer.expression.ExprString;

/**
 * A Literal String
 */
public interface LitString extends Literal,ExprString {

	public LitString duplicate();

	public void upperCase();
	
	public void fromBracket(boolean fromBracket);
	
	public boolean fromBracket();

}
