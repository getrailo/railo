package railo.runtime.sql.exp;

public class BracketExpression extends ExpressionSupport {

	private Expression exp;

	public BracketExpression(Expression exp) {
		this.exp=exp;
	}

	@Override
	public String toString(boolean noAlias) {
		if(noAlias || getIndex()==0)return "("+exp.toString(true) +")";
		return toString(true)+" as "+getIndex();
	}

	/**
	 * @return the exp
	 */
	public Expression getExp() {
		return exp;
	}

}
