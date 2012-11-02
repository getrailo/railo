package railo.transformer.cfml.evaluator.impl;

import railo.transformer.cfml.evaluator.ChildEvaluator;

/**
 * Prueft den Kontext des Tag <code>catch</code>.
 * Das Tag darf sich nur direkt innerhalb des Tag <code>try</code> befinden.
 */
public final class Catch extends ChildEvaluator {
	
	/**
	 * @see railo.transformer.cfml.evaluator.ChildEvaluator#getParentName()
	 */
	protected String getParentName() {
		return "try";
	}
	/*
	public void evaluate(Tag tag, TagLibTag libTag) throws EvaluatorException {
	
		String ns=libTag.getTagLib().getNameSpaceAndSeperator();
		String tryName=ns+"try";
		
		if(!ASMUtil.hasAncestorTag(tag,tryName))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be direct inside a "+tryName+" tag");
		
	}*/
}