package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagIf;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 *
 * Prueft den Kontext des Tag else.
 * Das Tag <code>else</code> darf nur direkt innerhalb des Tag <code>if</code> liegen.
 * Dem Tag <code>else</code> darf, innerhalb des Tag <code>if</code>, kein Tag <code>if</code> nachgestellt sein.
 * Das Tag darf auch nur einmal vorkommen innerhalb des Tag if. 
 */
public final class Else extends EvaluatorSupport {

	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag, TagLibTag libTag) throws EvaluatorException {
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
			String ifName=ns+"if";
	
		// check if tag is direct inside if
		if(!ASMUtil.isParentTag(tag, TagIf.class)) {
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be direct inside a "+ifName+" tag");
		}
			
		// check if is there a elseif tag after this tag
		if(ASMUtil.hasSisterTagAfter(tag,"elseif"))
			throw new EvaluatorException("Wrong Context, tag cfelseif can't be after tag else");
		// check if tag else is unique
		if(ASMUtil.hasSisterTagWithSameName(tag))
		 throw new EvaluatorException("Wrong Context, tag else must be once inside the tag if");
		
	}

}