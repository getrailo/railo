package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagIf;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;


/**
 * Prueft den Kontext des Tag elseif.
 * Das Tag <code>elseif</code> darf nur direkt innerhalb des Tag <code>if</code> liegen.  
 */
public final class ElseIf extends EvaluatorSupport {

	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag, TagLibTag libTag) throws EvaluatorException {
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
			String ifName=ns+"if";
		
		// check if tag is direct inside if
		if(!ASMUtil.isParentTag(tag, TagIf.class))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be direct inside a "+ifName+" tag");		
	}
}