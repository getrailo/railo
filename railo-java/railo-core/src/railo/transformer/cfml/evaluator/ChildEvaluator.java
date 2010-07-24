package railo.transformer.cfml.evaluator;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.library.tag.TagLibTag;


/**
 * checks the if a child tag is inside his parent
 */
public abstract class ChildEvaluator extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
	
	// check parent
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String name=ns+getParentName();
		
		if(!ASMUtil.hasAncestorTag(tag,name))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+name+" tag");
		
	}

	protected abstract String getParentName();

}