package railo.transformer.expression.literal;

import railo.transformer.expression.ExprInt;

/**
 * Literal Double Value
 */
public interface LitInteger extends Literal,ExprInt {
	
	public int geIntValue();
	
	public Integer getInteger();
}
