package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag case.
 * Das Tag <code>httpparam</code> darf nur innerhalb des Tag <code>http</code> liegen.
 */
public final class InvokeArgument extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
			String invokeName=ns+"invoke";
		
		// check if tag is direct inside if
		if(!ASMUtil.hasAncestorTag(tag,invokeName))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+invokeName+" tag");	
	}

}