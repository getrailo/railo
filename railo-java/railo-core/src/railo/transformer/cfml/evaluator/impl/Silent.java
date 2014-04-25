package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;

/**
 * Prueft den Kontext des Tag <code>try</code>.
 * Innerhalb des Tag try muss sich am Schluss 1 bis n Tags vom Typ catch befinden.
 */
public final class Silent extends EvaluatorSupport {
	
	@Override
	public void evaluate(Tag tag) throws EvaluatorException {
		ASMUtil.removeLiterlChildren(tag,true);
	}
}