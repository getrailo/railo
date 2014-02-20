package railo.transformer.bytecode;

import railo.runtime.op.Caster;
import railo.transformer.Factory;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.cast.CastDouble;
import railo.transformer.bytecode.cast.CastInt;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.var.DataMemberImpl;
import railo.transformer.bytecode.expression.var.NullExpression;
import railo.transformer.bytecode.literal.LitBooleanImpl;
import railo.transformer.bytecode.literal.LitDoubleImpl;
import railo.transformer.bytecode.literal.LitFloatImpl;
import railo.transformer.bytecode.literal.LitIntegerImpl;
import railo.transformer.bytecode.literal.LitLongImpl;
import railo.transformer.bytecode.literal.LitStringImpl;
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

public class BytecodeFactory extends Factory {
	
	private static BytecodeFactory instance=new BytecodeFactory();
	
	public static Factory getInstance() {
		return instance;
	}

	private final  LitBoolean TRUE;
	private final LitBoolean FALSE;
	private final LitString EMPTY;
	private final LitString NULL;
	private final LitDouble DOUBLE_ZERO;
	
	public BytecodeFactory(){
		TRUE=createLitBoolean(true);
		FALSE=createLitBoolean(false);
		EMPTY=createLitString("");
		NULL=createLitString("NULL");
		DOUBLE_ZERO=createLitDouble(0);
	}

	@Override
	public LitString createLitString(String str) {
		return new LitStringImpl(this,str,null,null);
	}

	@Override
	public LitString createLitString(String str, Position start, Position end) {
		return new LitStringImpl(this,str,start,end);
	}

	@Override
	public LitBoolean createLitBoolean(boolean b) {
		return new LitBooleanImpl(this, b, null, null);
	}

	@Override
	public LitBoolean createLitBoolean(boolean b, Position start, Position end) {
		return new LitBooleanImpl(this, b, start, end);
	}

	@Override
	public LitDouble createLitDouble(double d) {
		return new LitDoubleImpl(this, d, null, null);
	}

	@Override
	public LitDouble createLitDouble(double d, Position start, Position end) {
		return new LitDoubleImpl(this, d, start, end);
	}

	@Override
	public LitFloat createLitFloat(float f) {
		return new LitFloatImpl(this, f, null, null);
	}

	@Override
	public LitFloat createLitFloat(float f, Position start, Position end) {
		return new LitFloatImpl(this, f, start, end);
	}

	@Override
	public LitLong createLitLong(long l) {
		return new LitLongImpl(this, l, null, null);
	}

	@Override
	public LitLong createLitLong(long l, Position start, Position end) {
		return new LitLongImpl(this, l, start, end);
	}

	@Override
	public LitInteger createLitInteger(int i) {
		return new LitIntegerImpl(this, i, null, null);
	}

	@Override
	public LitInteger createLitInteger(int i, Position start, Position end) {
		return new LitIntegerImpl(this, i, start, end);
	}

	@Override
	public Expression createNullExpression() {
		return new NullExpression(this);
	}

	@Override
	public DataMember createDataMember(ExprString name) {
		return new DataMemberImpl(name);
	}

	@Override
	public Literal createLiteral(Object obj,Literal defaultValue) {
		if(obj instanceof Boolean) return createLitBoolean(((Boolean)obj).booleanValue());
		if(obj instanceof Number) {
			if(obj instanceof Float)return createLitFloat(((Float)obj).floatValue());
			else if(obj instanceof Integer)return createLitInteger(((Integer)obj).intValue());
			else if(obj instanceof Long)return createLitLong(((Long)obj).longValue());
			else return createLitDouble(((Number)obj).doubleValue());
		}
		String str = Caster.toString(obj,null);
		if(str!=null) return createLitString(str);
		return defaultValue;
	}

	@Override
	public LitBoolean TRUE() {
		return TRUE;
	}

	@Override
	public LitBoolean FALSE() {
		return FALSE;
	}

	@Override
	public LitString EMPTY() {
		return EMPTY;
	}

	@Override
	public LitDouble DOUBLE_ZERO() {
		return DOUBLE_ZERO;
	}

	@Override
	public LitString NULL() {
		return NULL;
	}

	@Override
	public ExprDouble toExprDouble(Expression expr) {
		return CastDouble.toExprDouble(expr);
	}

	@Override
	public ExprString toExprString(Expression expr) {
		return CastString.toExprString(expr);
	}

	@Override
	public ExprBoolean toExprBoolean(Expression expr) {
		return CastBoolean.toExprBoolean(expr);
	}

	@Override
	public ExprInt toExprInt(Expression expr) {
		return CastInt.toExprInt(expr);
	}
}
