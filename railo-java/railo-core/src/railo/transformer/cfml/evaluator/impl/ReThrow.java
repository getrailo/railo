package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag rethrow.
 * Das Tag <code>rethrow</code> darf nur innerhalb des Tag <code>throw</code> liegen.
 */
public final class ReThrow extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String queryName=ns+"catch";
		
		
		if(!ASMUtil.hasAncestorTryStatement(tag)){
			if(tag.isScriptBase())
				throw new EvaluatorException("Wrong Context, statement "+libTag.getName()+" must be inside a "+queryName+" tag or catch statement");
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+queryName+" tag");
			
		}
		//ASMUtil.replace(tag,new TagReThrow(tag));
	}

}