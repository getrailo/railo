package railo.transformer.cfml.attributes;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.library.tag.TagLibTag;

/**
 * to make addional evaluations of the attributes of a tag
 */
public interface AttributeEvaluator {

	/**
	 * @param tagLibTag
	 * @param tag
	 * @throws AttributeEvaluatorException
	 */
	void evaluate( TagLibTag tagLibTag, Tag tag) throws AttributeEvaluatorException;
}