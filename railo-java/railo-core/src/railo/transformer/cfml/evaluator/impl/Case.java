package railo.transformer.cfml.evaluator.impl;

import railo.transformer.cfml.evaluator.ChildEvaluator;



/**
 * Prueft den Kontext des Tag case.
 * Das Tag <code>case</code> darf nur direkt innerhalb des Tag <code>switch</code> liegen.
 */
public final class Case extends ChildEvaluator {

	/**
	 * @see railo.transformer.cfml.evaluator.ChildEvaluator#getParentName()
	 */
	protected String getParentName() {
		return "switch";
	}
}