package railo.transformer.cfml.evaluator.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.runtime.functions.list.ListQualify;
import railo.runtime.functions.other.PreserveSingleQuotes;
import railo.runtime.functions.other.QuotedValueList;
import railo.runtime.functions.query.ValueList;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastOther;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.expression.var.Member;
import railo.transformer.bytecode.expression.var.UDF;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
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
					
					if(expr instanceof Variable) {
						// do not preserve BIF PreserveSingleQuotes return value
						Member member = ((Variable)expr).getFirstMember();
						if(member instanceof BIF) {
							BIF bif=(BIF) member;

							if(bif.getClazz().getName().equals(PreserveSingleQuotes.class.getName())) {
								printOut.setExpr(bif.getArguments()[0].getValue());
								continue;
							}
							else if(bif.getClazz().getName().equals(ListQualify.class.getName())) {
								Argument[] args = bif.getArguments();
								List<Argument> arr=new ArrayList<Argument>();
								
								// first get existing arguments
								arr.add(args[0]);
								arr.add(args[1]);
								if(args.length>=3)arr.add(args[2]);
								else arr.add(new Argument(LitString.toExprString(","),"string"));
								if(args.length>=4)arr.add(args[3]);
								else arr.add(new Argument(LitString.toExprString("all"),"string"));
								if(args.length>=5)arr.add(args[4]);
								else arr.add(new Argument(LitBoolean.toExprBoolean(false),"boolean"));
								
								
								// PSQ-BIF DO NOT REMOVE THIS COMMENT
								arr.add(new Argument(LitBoolean.toExprBoolean(true),"boolean"));
								bif.setArguments(arr.toArray(new Argument[arr.size()]));
								continue;
							}
							else if(
								bif.getClazz().getName().equals(QuotedValueList.class.getName()) ||
								bif.getClazz().getName().equals(ValueList.class.getName())
								) {
								//printOut.setPreserveSingleQuote(false);
								continue;
							}
						}
						
						// do not preserve UDF return value
						member= ((Variable)expr).getLastMember();
						if(member instanceof UDF) continue;
					}
					printOut.setCheckPSQ(true);
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
					expr instanceof CastOther && 
					(
							((CastOther) expr).getType().equalsIgnoreCase("String") || 
							((CastOther) expr).getType().equalsIgnoreCase("java.lang.String")
					)
				){
					expr=((CastOther) expr).getExpr();
			}
			else break;
		}
		return expr;
	}
}




