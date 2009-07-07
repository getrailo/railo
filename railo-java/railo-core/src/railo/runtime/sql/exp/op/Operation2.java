package railo.runtime.sql.exp.op;

import railo.runtime.sql.exp.Expression;
import railo.runtime.sql.exp.ExpressionSupport;

public class Operation2 extends ExpressionSupport implements Operation {

	private Expression left;
	private Expression right;
	private int operator;


	public Operation2(Expression left, Expression right,int operator) {
		this.left=left;
		this.right=right;
		this.operator=operator;
	}


	public static String toString(int operator) {
		switch(operator) {
		case Operation.OPERATION2_DIVIDE: return "/";
		case Operation.OPERATION2_MINUS: return "-";
		case Operation.OPERATION2_MULTIPLY: return "*";
		case Operation.OPERATION2_PLUS: return "+";
		case Operation.OPERATION2_EXP: return "^";
		case Operation.OPERATION2_MOD: return "%";
		
		case Operation.OPERATION2_AND: return "and";
		case Operation.OPERATION2_OR: return "or";
		case Operation.OPERATION2_XOR: return "xor";

		case Operation.OPERATION2_EQ: return "=";
		case Operation.OPERATION2_GT: return ">";
		case Operation.OPERATION2_GTE: return "=>";
		case Operation.OPERATION2_LT: return "<";
		case Operation.OPERATION2_LTE: return "<=";
		case Operation.OPERATION2_LTGT: return "<>";
		case Operation.OPERATION2_NEQ: return "!=";
		case Operation.OPERATION2_NOT_LIKE: return "not like";
		case Operation.OPERATION2_LIKE: return "like";

		case Operation.OPERATION1_PLUS: return "+";
		case Operation.OPERATION1_MINUS: return "-";
		case Operation.OPERATION1_NOT: return "not";
		case Operation.OPERATION1_IS_NOT_NULL: return "is not null";
		case Operation.OPERATION1_IS_NULL: return "is null";
		}
		return null;
	}


	public String toString(boolean noAlias) {
		if(noAlias || getIndex()==0)return left.toString(true)+" "+toString(operator)+" "+right.toString(true);
		return toString(true)+" as "+getAlias();
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
