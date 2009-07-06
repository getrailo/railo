package railo.transformer.cfml.evaluator.impl;

import java.util.Iterator;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.Cast;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.expression.var.Member;
import railo.transformer.bytecode.expression.var.UDF;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;


/**
 * sign print outs for preserver
*/
public final class Query extends EvaluatorSupport {

	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element)
	 */
	public void evaluate(Tag tag) throws EvaluatorException { 
		translateChildren(tag.getBody().getStatements().iterator());
	}
	
	private void translateChildren(Iterator it) {
		Statement stat;
		
		while(it.hasNext()) {
			stat=(Statement) it.next();
			if(stat instanceof PrintOut) {
				PrintOut printOut = ((PrintOut)stat);
				Expression e = printOut.getExpr();
				if(!(e instanceof Literal)) {
					Expression expr=removeCastString(e);
					
					/* / do not preserve UDF return value
					print.ln(expr.getClass().getName());
					if(expr instanceof CastString) {
						CastString cs = (CastString)expr;
						Expression child = cs.getExpr();
						if(child instanceof Variable) {
							if(((Variable)child).getLastMember() instanceof UDF)
								continue;
						}
					}*/
					
					if(expr instanceof Variable) {
						// do not preserve BIF PreserveSingleQuotes return value
						Member member = ((Variable)expr).getFirstMember();
						if(member instanceof BIF) {
							BIF bif=(BIF) member;
							if(bif.getClassName().equals("railo.runtime.functions.other.PreserveSingleQuotes")) {
								printOut.setExpr(bif.getArguments()[0].getValue());
								continue;
							}
						}
						
						// do not preserve UDF return value
						member= ((Variable)expr).getLastMember();
						if(member instanceof UDF) continue;
					}
					printOut.setPreserveSingleQuote(true);
					if(e!=expr)printOut.setExpr(expr);
				}
			}
			else if(stat instanceof Tag){
				Body b=((Tag)stat).getBody();
				if(b!=null) 
					translateChildren(b.getStatements().iterator());
			}
			else if(stat instanceof Body){
				translateChildren(((Body)stat).getStatements().iterator());
			}
		}
	}

	private Expression removeCastString(Expression expr) {
		while(true) {
			if(expr instanceof CastString){
				expr=((CastString)expr).getExpr();
				
			}
			else if(
					expr instanceof Cast && 
					(
							((Cast) expr).getType().equalsIgnoreCase("String") || 
							((Cast) expr).getType().equalsIgnoreCase("java.lang.String")
					)
				){
					expr=((Cast) expr).getExpr();
			}
			else break;
		}
		return expr;
	}
}




