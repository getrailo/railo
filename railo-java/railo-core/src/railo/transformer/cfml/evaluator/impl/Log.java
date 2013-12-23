package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;

public final class Log extends EvaluatorSupport {
	


	/**
	 *
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(railo.transformer.bytecode.statement.tag.Tag, railo.transformer.library.tag.TagLibTag, railo.transformer.library.function.FunctionLib[])
	 */
	public void evaluate(Tag tag,TagLibTag tagLibTag,FunctionLib[] flibs) throws EvaluatorException {
		//TagLoop loop=(TagLoop) tag;
		// attribute text or exception must be defined
        if(!tag.containsAttribute("attributecollection") && !tag.containsAttribute("text") && !tag.containsAttribute("exception"))
        	throw new EvaluatorException("Wrong Context, you must define one of the following attributes [text,exception]");
	}
}
