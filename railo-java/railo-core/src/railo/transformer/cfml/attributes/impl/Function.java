/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.transformer.cfml.attributes.impl;

import railo.transformer.bytecode.cast.CastBoolean;
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
public final class Function implements AttributeEvaluator {

	/**
	 * @see railo.transformer.cfml.attributes.AttributeEvaluator#evaluate(railo.transformer.library.tag.TagLibTag, org.w3c.dom.Element)
	 */
	public TagLibTag evaluate( TagLibTag tagLibTag, Tag tag) throws AttributeEvaluatorException {
		tagLibTag.setParseBody(false);
		
		Attribute attrOutput = tag.getAttribute("output");
		if(attrOutput==null) return tagLibTag;
		
		Expression expr = CastBoolean.toExprBoolean(attrOutput.getValue());
		
		if(!(expr instanceof LitBoolean))
			throw new AttributeEvaluatorException("Attribute output of the Tag Function, must be a literal boolean value (true or false)");
		boolean output = ((LitBoolean)expr).getBooleanValue();
		if(output)
			tagLibTag.setParseBody(true);
		
		return tagLibTag;
	}
}