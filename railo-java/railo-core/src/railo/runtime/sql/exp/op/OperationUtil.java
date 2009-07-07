package railo.runtime.sql.exp.op;

public class OperationUtil {
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
		case Operation.OPERATION2_GTE: return ">=";
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
}
