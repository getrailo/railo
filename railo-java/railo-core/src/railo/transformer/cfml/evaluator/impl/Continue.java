package railo.transformer.cfml.evaluator.impl;

import railo.commons.lang.StringUtil;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagContinue;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag continue.
 * Das Tag <code>break</code> darf nur innerhalb des Tag <code>loop, while, foreach</code> liegen.
 */
public final class Continue extends EvaluatorSupport {

	@Override
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String loopName=ns+"loop";
		String whileName=ns+"while";
		
		// label
		String label=null;
		Attribute attrLabel = tag.getAttribute("label");
		if(attrLabel!=null){
			TagContinue tc=(TagContinue) tag;
			label=Break.variableToString(tag,attrLabel,null);
			if(label!=null){
				tc.setLabel(label=label.trim());
				tag.removeAttribute("label");
			}
			else if(ASMUtil.isLiteralAttribute(tag, attrLabel, ASMUtil.TYPE_STRING, false, true)) {
				LitString ls=(LitString) CastString.toExprString(tag.getAttribute("label").getValue());
				label = ls.getString();
				if(!StringUtil.isEmpty(label,true)) {
					tc.setLabel(label=label.trim());
					tag.removeAttribute("label");
				}
				else label=null;
			}
		}
		
		if(ASMUtil.isLiteralAttribute(tag, "label", ASMUtil.TYPE_STRING, false, true)) {
			LitString ls=(LitString) CastString.toExprString(tag.getAttribute("label").getValue());
			TagContinue tc=(TagContinue) tag;
			label = ls.getString();
			if(!StringUtil.isEmpty(label,true)) {
				tc.setLabel(label=label.trim());
				tag.removeAttribute("label");
			}
			else label=null;
		}
		
		if(!ASMUtil.hasAncestorContinueFCStatement(tag,label)) {
			if(tag.isScriptBase()) {
				if(StringUtil.isEmpty(label))
					throw new EvaluatorException("Wrong Context, "+libTag.getName()+" must be inside a loop (for,while,loop ...)");
				throw new EvaluatorException("Wrong Context, "+libTag.getName()+" must be inside a loop (for,while,loop ...) with the label ["+label+"]");
				
			}
			if(StringUtil.isEmpty(label))
				throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+loopName+" or "+whileName+" tag");
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+loopName+" or "+whileName+" tag with the label ["+label+"]");
			
		}
	}
}