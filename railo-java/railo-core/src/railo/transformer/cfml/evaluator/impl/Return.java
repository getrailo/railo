package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag case.
 * Das Tag <code>return</code> darf nur innerhalb des Tag <code>function</code> liegen.
 */
public final class Return extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		//String ns=libTag.getTagLib().getNameSpaceAndSeperator();
		//	String funcName=ns+"function";
		
		// check if tag is direct inside if
		//if(!ASMUtil.hasAncestorTag(tag,funcName))
		//	throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+funcName+" tag");	
	}

}