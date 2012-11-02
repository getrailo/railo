package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag mailpart.
 * Das Tag <code>mailPart</code> darf nur innerhalb des Tag <code>mail</code> liegen.
 */
public final class MailPart extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
	
	// check parent
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String mailName=ns+"mail";
		
		if(!ASMUtil.hasAncestorTag(tag,mailName))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+mailName+" tag");
	}
}