package railo.transformer.bytecode.expression.var;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Type;

import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.ScopeFactory;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.Identifier;
import railo.transformer.bytecode.literal.LitString;

public final class VariableString extends ExpressionBase implements ExprString {

	private Expression expr;

	public VariableString(Expression expr) {
		super(expr.getStart(),expr.getEnd());
		this.expr=expr;
	}
 
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		return translateVariableToExprString(expr,false).writeOut(bc, mode);
	}

	public static ExprString toExprString(Expression expr) {
		if(expr instanceof ExprString) return (ExprString) expr;
		return new VariableString(expr);
	}
	
	public static ExprString translateVariableToExprString(Expression expr, boolean rawIfPossible) throws BytecodeException {
		if(expr instanceof ExprString) return (ExprString) expr;
		return LitString.toExprString(translateVariableToString(expr,rawIfPossible), expr.getStart(),expr.getEnd());
	}
	
	private static String translateVariableToString(Expression expr, boolean rawIfPossible) throws BytecodeException {
		if(!(expr instanceof Variable)) throw new BytecodeException("can't translate value to a string",expr.getStart());
		return variableToString((Variable) expr,rawIfPossible);
	}
		

	public static String variableToString(Variable var, boolean rawIfPossible) throws BytecodeException {
		return railo.runtime.type.util.ListUtil.arrayToList(variableToStringArray(var,rawIfPossible),".");
	}
	public static String[] variableToStringArray(Variable var, boolean rawIfPossible) throws BytecodeException {
		List members = var.getMembers();
			
		List<String> arr=new ArrayList<String>();
		if(var.getScope()!=Scope.SCOPE_UNDEFINED)arr.add(ScopeFactory.toStringScope(var.getScope(),"undefined"));
		Iterator it = members.iterator();
		DataMember dm;
		Expression n;
		while(it.hasNext()) {
			Object o = it.next();
			if(!(o instanceof DataMember)) throw new BytecodeException("can't translate Variable to a String",var.getStart());
			dm=(DataMember) o;
			n=dm.getName();
			if(n instanceof Literal) {
				if(rawIfPossible && n instanceof Identifier) {
					arr.add(((Identifier) n).getRaw());
				}
				else {
					arr.add(((Literal) n).getString());
				}
			}
			else throw new BytecodeException("argument name must be a constant value",var.getStart());
		}
		return arr.toArray(new String[arr.size()]);
	}
	
	public String castToString() throws BytecodeException{
		return translateVariableToString(expr,false);
	}
}
