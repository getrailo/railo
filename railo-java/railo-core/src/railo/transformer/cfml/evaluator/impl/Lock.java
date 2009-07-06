package railo.transformer.cfml.evaluator.impl;

import railo.runtime.op.Caster;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;

public class Lock extends EvaluatorSupport {
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag) throws EvaluatorException { 
		tag.addAttribute(
				new Attribute(
						false,
						"id",
						LitString.toExprString(Caster.toString((int)(Math.random()*100000))),
						"string"
				));
	}
}
