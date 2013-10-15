package railo.transformer.cfml.evaluator.impl;

import railo.commons.lang.StringUtil;
import railo.transformer.bytecode.cast.Cast;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.expression.var.VariableString;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagBreak;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag break.
 * Das Tag <code>break</code> darf nur innerhalb des Tag <code>loop, while, foreach</code> liegen.
 */
public final class Break extends EvaluatorSupport {


	@Override
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String loopName=ns+"loop";
		String whileName=ns+"while";
		

		// label
		String label=null;
		
		Attribute attrLabel = tag.getAttribute("label");
		if(attrLabel!=null){
			TagBreak tb=(TagBreak) tag;
			label=variableToString(tag,attrLabel,null);
			if(label!=null){
				tb.setLabel(label=label.trim());
				tag.removeAttribute("label");
			}
			
			else if(ASMUtil.isLiteralAttribute(tag, attrLabel, ASMUtil.TYPE_STRING, false, true)) {
				LitString ls=(LitString) CastString.toExprString(tag.getAttribute("label").getValue());
				label = ls.getString();
				if(!StringUtil.isEmpty(label,true)) {
					tb.setLabel(label=label.trim());
					tag.removeAttribute("label");
				}
				else label=null;
			}
		}
		
		// no base tag found
		if(!ASMUtil.hasAncestorBreakFCStatement(tag,label)) {
			if(tag.isScriptBase()) {
				if(StringUtil.isEmpty(label))
					throw new EvaluatorException("Wrong Context, "+libTag.getName()+" must be inside a looping statement or tag");
				throw new EvaluatorException("Wrong Context, "+libTag.getName()+" must be inside a looping statement or tag with the label ["+label+"]");
				
			}
			
			
			if(StringUtil.isEmpty(label))
				throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+loopName+" or "+whileName+" tag");
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+loopName+" or "+whileName+" tag with the label ["+label+"]");
			
		}

	}

	static String variableToString(Tag tag, Attribute attrLabel, String defaultValue) {
		Expression value = attrLabel.getValue();
		while(value instanceof Cast) value=((Cast)value).getExpr();
		if(value instanceof Variable) {
			Variable var=(Variable)value;
			try {
				return VariableString.variableToString(var, true);
			} catch (Throwable t) {}
		}
		return defaultValue;
	}

}