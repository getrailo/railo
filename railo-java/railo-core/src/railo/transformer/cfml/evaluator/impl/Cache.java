package railo.transformer.cfml.evaluator.impl;

import railo.runtime.op.Caster;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;

public class Cache extends EvaluatorSupport {

	@Override
	public void evaluate(Tag tag) throws EvaluatorException { 
		tag.addAttribute(
				new Attribute(
						false,
						"_id",
						tag.getFactory().createLitString(Caster.toString((int)(Math.random()*100000))),
						"string"
				));
	}
}
