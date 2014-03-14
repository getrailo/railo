package railo.transformer.bytecode.expression.var;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Type;

import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.ScopeFactory;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.Identifier;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.Literal;
import railo.transformer.expression.var.DataMember;
import railo.transformer.expression.var.Variable;

public final class VariableString extends ExpressionBase implements ExprString {

	private Expression expr;

	public VariableString(Expression expr) {
		super(expr.getFactory(),expr.getStart(),expr.getEnd());
		this.expr=expr;
	}
 
	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
		return translateVariableToExprString(expr,false).writeOut(bc, mode);
	}

	public static ExprString toExprString(Expression expr) {
		if(expr instanceof ExprString) return (ExprString) expr;
		return new VariableString(expr);
	}
	
	public static ExprString translateVariableToExprString(Expression expr, boolean rawIfPossible) throws TransformerException {
		if(expr instanceof ExprString) return (ExprString) expr;
		return expr.getFactory().createLitString(translateVariableToString(expr,rawIfPossible), expr.getStart(),expr.getEnd());
	}
	
	private static String translateVariableToString(Expression expr, boolean rawIfPossible) throws TransformerException {
		if(!(expr instanceof Variable)) throw new TransformerException("can't translate value to a string",expr.getStart());
		return variableToString((Variable) expr,rawIfPossible);
	}
		

	public static String variableToString(Variable var, boolean rawIfPossible) throws TransformerException {
		return railo.runtime.type.util.ListUtil.arrayToList(variableToStringArray(var,rawIfPossible),".");
	}
	public static String[] variableToStringArray(Variable var, boolean rawIfPossible) throws TransformerException {
		List members = var.getMembers();
			
		List<String> arr=new ArrayList<String>();
		if(var.getScope()!=Scope.SCOPE_UNDEFINED)arr.add(ScopeFactory.toStringScope(var.getScope(),"undefined"));
		Iterator it = members.iterator();
		DataMember dm;
		Expression n;
		while(it.hasNext()) {
			Object o = it.next();
			if(!(o instanceof DataMember)) throw new TransformerException("can't translate Variable to a String",var.getStart());
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
			else throw new TransformerException("argument name must be a constant value",var.getStart());
		}
		return arr.toArray(new String[arr.size()]);
	}
	
	public String castToString() throws TransformerException{
		return translateVariableToString(expr,false);
	}
}
