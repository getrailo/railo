package railo.runtime.sql.exp.op;

import railo.runtime.sql.exp.Expression;
import railo.runtime.sql.exp.ExpressionSupport;

public class Operation1 extends ExpressionSupport implements Operation {

	private Expression exp;
	private int operator;


	/**
	 * @return the exp
	 */
	public Expression getExp() {
		return exp;
	}

	/**
	 * @return the operator
	 */
	public int getOperator() {
		return operator;
	}

	public Operation1(Expression exp, int operator) {
		this.exp=exp;
		this.operator=operator;
	}

	public String toString(boolean noAlias) {
		if(!hasAlias() || noAlias) {
			if(operator==OPERATION1_IS_NULL || operator==OPERATION1_IS_NOT_NULL) {
				return exp.toString(true)+" "+Operation2.toString(operator);
			}
			return Operation2.toString(operator)+" "+exp.toString(true);
		}
		return toString(true)+" as "+getAlias();
	}
	
}