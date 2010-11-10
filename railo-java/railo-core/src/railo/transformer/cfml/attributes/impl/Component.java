package railo.transformer.cfml.attributes.impl;

import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.attributes.AttributeEvaluator;
import railo.transformer.cfml.attributes.AttributeEvaluatorException;
import railo.transformer.library.tag.TagLibTag;

/**
 * Attribute Evaluator for the tag Function
 */
public final class Component implements AttributeEvaluator {

    /**
     * @see railo.transformer.cfml.attributes.AttributeEvaluator#evaluate(railo.transformer.library.tag.TagLibTag, org.w3c.dom.Element)
     */
    public TagLibTag evaluate( TagLibTag tagLibTag, Tag tag) throws AttributeEvaluatorException {
		tagLibTag.setParseBody(false);
		Attribute attr = tag.getAttribute("output");
		if(attr!=null) {
			Expression expr = attr.getValue();
			
			if(!(expr instanceof LitBoolean))
				throw new AttributeEvaluatorException("Attribute output of the Tag Component, must be a static boolean value (true or false)");
			if(((LitBoolean)expr).getBooleanValue())
				tagLibTag.setParseBody(true);
		}
        return tagLibTag;
    }
}