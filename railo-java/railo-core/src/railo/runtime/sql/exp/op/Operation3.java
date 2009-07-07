package railo.runtime.sql.exp.op;

import railo.runtime.sql.exp.Expression;
import railo.runtime.sql.exp.ExpressionSupport;

public class Operation3 extends ExpressionSupport implements Operation {

	private Expression exp;
	private Expression left;
	private Expression right;
	private int operator;


	public Operation3(Expression exp, Expression left, Expression right, int operator) {
		this.exp=exp;
		this.left=left;
		this.right=right;
		this.operator=operator;
	}

	public String toString(boolean noAlias) {
		// like escape
		if(Operation.OPERATION3_LIKE==operator){
			if(!hasAlias() || noAlias) {
				return exp.toString(true)+" like "+
						left.toString(true)+" escape "+
						right.toString(true);
			}
			return toString(true)+" as "+getAlias();
		}
		// between
		if(!hasAlias() || noAlias) {
			return exp.toString(true)+" between "+left.toString(true)+" and "+right.toString(true);
		}
		return toString(true)+" as "+getAlias();
	}

	/**
	 * @return the exp
	 */
	public Expression getExp() {
		return exp;
	}

	/**
	 * @return the left
	 */
	public Expression getLeft() {
		return left;
	}

	/**
	 * @return the operator
	 */
	public int getOperator() {
		return operator;
	}

	/**
	 * @return the right
	 */
	public Expression getRight() {
		return right;
	}
}
