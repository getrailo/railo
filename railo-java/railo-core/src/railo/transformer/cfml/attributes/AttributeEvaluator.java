package railo.transformer.cfml.attributes;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.library.tag.TagLibTag;

/**
 * to make additional evaluations of the attributes of a tag
 */
public interface AttributeEvaluator {

	/**
	 * @param tagLibTag
	 * @param tag
	 * @throws AttributeEvaluatorException
	 */
	TagLibTag evaluate( TagLibTag tagLibTag, Tag tag) throws AttributeEvaluatorException;
}