package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.ChildEvaluator;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag mailparam.
 * Das Tag <code>mailParam</code> darf nur innerhalb des Tag <code>mail</code> liegen.
 */
public final class MailParam extends ChildEvaluator {

	protected String getParentName() {
		return "mail";
	}

//ç
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		/*
		// check attributes
		boolean hasFile=tag.containsAttribute("file");
		boolean hasName=tag.containsAttribute("name");
		// both attributes
		if(hasName && hasFile) {
			throw new EvaluatorException("Wrong Context for tag "+libTag.getFullName()+", when you use attribute file you can't also use attribute name");
		}
		// no attributes
		if(!hasName && !hasFile) {
			throw new EvaluatorException("Wrong Context for tag "+libTag.getFullName()+", you must use attribute file or name for this tag");
		}*/
	}
}