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
package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.ChildEvaluator;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag mailparam.
 * Das Tag <code>mailParam</code> darf nur innerhalb des Tag <code>mail</code> liegen.
 */
public final class MailParam extends ChildEvaluator {

	protected String getParentName() {
		return "mail";
	}

//�
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		/*
		// check attributes
		boolean hasFile=tag.containsAttribute("file");
		boolean hasName=tag.containsAttribute("name");
		// both attributes
		if(hasName && hasFile) {
			throw new EvaluatorException("Wrong Context for tag "+libTag.getFullName()+", when you use attribute file you can't also use attribute name");
		}
		// no attributes
		if(!hasName && !hasFile) {
			throw new EvaluatorException("Wrong Context for tag "+libTag.getFullName()+", you must use attribute file or name for this tag");
		}*/
	}
}