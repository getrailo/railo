package railo.transformer.bytecode.expression.var;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Type;

import railo.runtime.type.Scope;
import railo.runtime.type.scope.ScopeFactory;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitString;

public final class VariableString extends ExpressionBase implements ExprString {

	private Expression expr;

	public VariableString(Expression expr) {
		super(expr.getLine());
		this.expr=expr;
	}
 
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		return translateVariableToExprString(expr).writeOut(bc, mode);
	}

	public static ExprString toExprString(Expression expr) {
		if(expr instanceof ExprString) return (ExprString) expr;
		return new VariableString(expr);
	}
	
	private static ExprString translateVariableToExprString(Expression expr) throws BytecodeException {
		if(expr instanceof ExprString) return (ExprString) expr;
		return LitString.toExprString(translateVariableToString(expr), expr.getLine());
	}
	private static String translateVariableToString(Expression expr) throws BytecodeException {
		if(!(expr instanceof Variable)) throw new BytecodeException("can't translate value to a string",expr.getLine());
		Variable var=(Variable) expr;
		List members = var.getMembers();
			
		StringBuffer sb=new StringBuffer();
		if(var.getScope()!=Scope.SCOPE_UNDEFINED)sb.append(ScopeFactory.toStringScope(var.getScope()));
		Iterator it = members.iterator();
		DataMember dm;
		Expression n;
		Literal l;
		while(it.hasNext()) {
			Object o = it.next();
			if(!(o instanceof DataMember)) throw new BytecodeException("can't translate Variable to a String",var.getLine());
			dm=(DataMember) o;
			n=dm.getName();
			if(n instanceof Literal) {
				l=(Literal) n;
				if(sb.length()>0)sb.append('.');
				sb.append(l.getString());
			}
			else throw new BytecodeException("argument name must be a constant value",var.getLine());
		}
		return sb.toString();
	}
	
	public String castToString() throws BytecodeException{
		return translateVariableToString(expr);
	}
}
