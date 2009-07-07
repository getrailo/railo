package railo.runtime.sql.exp.op;

import railo.runtime.sql.exp.Expression;

public interface Operation extends Expression {

	public static final int OPERATION2_PLUS=0;
	public static final int OPERATION2_MINUS=1;
	public static final int OPERATION2_MULTIPLY=2;
	public static final int OPERATION2_DIVIDE=3;
	public static final int OPERATION2_EXP=4;
	public static final int OPERATION2_MOD=5;
	
	public static final int OPERATION2_XOR=10;
	public static final int OPERATION2_OR=11;
	public static final int OPERATION2_AND=12;

	public static final int OPERATION2_EQ=13;
	public static final int OPERATION2_NEQ=14;
	public static final int OPERATION2_LT=15;
	public static final int OPERATION2_LTE=16;
	public static final int OPERATION2_GT=17;
	public static final int OPERATION2_GTE=18;
	public static final int OPERATION2_LTGT=19;
	public static final int OPERATION2_NOT_LIKE=20;
	public static final int OPERATION2_LIKE=21;

	public static final int OPERATION1_PLUS=30;
	public static final int OPERATION1_MINUS=31;
	public static final int OPERATION1_NOT=32;
	public static final int OPERATION1_IS_NULL=33;
	public static final int OPERATION1_IS_NOT_NULL=34;
	
	public static final int OPERATION3_BETWEEN=50;
	public static final int OPERATION3_LIKE = 51;
}
