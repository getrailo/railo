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

import railo.transformer.cfml.evaluator.ChildEvaluator;

/**
 * Prueft den Kontext des Tag <code>catch</code>.
 * Das Tag darf sich nur direkt innerhalb des Tag <code>try</code> befinden.
 */
public final class Catch extends ChildEvaluator {
	
	/**
	 * @see railo.transformer.cfml.evaluator.ChildEvaluator#getParentName()
	 */
	protected String getParentName() {
		return "try";
	}
	/*
	public void evaluate(Tag tag, TagLibTag libTag) throws EvaluatorException {
	
		String ns=libTag.getTagLib().getNameSpaceAndSeperator();
		String tryName=ns+"try";
		
		if(!ASMUtil.hasAncestorTag(tag,tryName))
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be direct inside a "+tryName+" tag");
		
	}*/
}