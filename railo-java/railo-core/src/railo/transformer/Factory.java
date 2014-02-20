package railo.transformer;

import railo.transformer.bytecode.Position;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.ExprDouble;
import railo.transformer.expression.ExprInt;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitBoolean;
import railo.transformer.expression.literal.LitDouble;
import railo.transformer.expression.literal.LitFloat;
import railo.transformer.expression.literal.LitInteger;
import railo.transformer.expression.literal.LitLong;
import railo.transformer.expression.literal.LitString;
import railo.transformer.expression.literal.Literal;
import railo.transformer.expression.var.DataMember;

public abstract class Factory {
	

	public abstract  LitBoolean TRUE();
	public abstract  LitBoolean FALSE();
	public abstract  LitString EMPTY();
	public abstract LitDouble DOUBLE_ZERO();
	public abstract LitString NULL();

	
	public abstract LitString createLitString(String str);
	public abstract LitString createLitString(String str, Position start, Position end);

	public abstract LitBoolean createLitBoolean(boolean b);
	public abstract LitBoolean createLitBoolean(boolean b, Position start,Position end);

	public abstract LitDouble createLitDouble(double d);
	public abstract LitDouble createLitDouble(double d, Position start,Position end);
	

	public abstract LitFloat createLitFloat(float f);
	public abstract LitFloat createLitFloat(float f, Position start,Position end);
	
	public abstract LitLong createLitLong(long l);
	public abstract LitLong createLitLong(long l, Position start,Position end);

	public abstract LitInteger createLitInteger(int i);
	public abstract LitInteger createLitInteger(int i, Position start,Position end);
	
	public abstract Expression createNullExpression();
	
	public abstract Literal createLiteral(Object obj,Literal defaultValue);

	public abstract ExprDouble toExprDouble(Expression expr);
	public abstract ExprString toExprString(Expression expr);
	public abstract ExprBoolean toExprBoolean(Expression expr);
	public abstract ExprInt toExprInt(Expression expr);
	
	
	public abstract DataMember createDataMember(ExprString name);

}
