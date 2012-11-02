package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * TODO remove
 * Prueft den Kontext des Tag queryparam.
 * Das Tag <code>queryParam</code> darf nur innerhalb des Tag <code>loop, while, foreach</code> liegen.
 */
public final class QueryParam extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String queryName=ns+"query";
		
		if(!ASMUtil.hasAncestorTag(tag,queryName))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+queryName+" tag");
	}

}