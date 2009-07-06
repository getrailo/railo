package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prüft den Kontext des Tag break.
 * Das Tag <code>break</code> darf nur innerhalb des Tag <code>loop, while, foreach</code> liegen.
 */
public final class Break extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		//print.ln("start"+tag);
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String loopName=ns+"loop";
		String whileName=ns+"while";
		
		
		if(!ASMUtil.hasAncestorLoopStatement(tag))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+loopName+" or "+whileName+" tag");

	}

}