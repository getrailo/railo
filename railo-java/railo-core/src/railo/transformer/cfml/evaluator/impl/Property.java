package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag mailparam.
 * Das Tag <code>mailParam</code> darf nur innerhalb des Tag <code>mail</code> liegen.
 */
public final class Property extends EvaluatorSupport {

	@Override
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
	
	// check parent
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String compName=ns+"component";
		
		if(!ASMUtil.isParentTag(tag,compName))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside "+compName+" tag");
	}
}