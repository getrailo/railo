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

import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.TryCatchFinally;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag break.
 * Das Tag <code>break</code> darf nur innerhalb des Tag <code>loop, while, foreach</code> liegen.
 */
public final class Retry extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		String ns=libTag.getTagLib().getNameSpaceAndSeparator();
		String name=ns+"catch";
		
		if(getAncestorCatch(libTag.getTagLib(),tag)==null)
			throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()+" must be inside a "+name+" tag");
	}
	
	public static Statement getAncestorCatch(TagLib tagLib, Statement stat) {
		String name=tagLib.getNameSpaceAndSeparator()+"catch";
		Tag tag;
		Statement parent=stat;
		while(true)	{
			parent=parent.getParent();
			if(parent==null)return null;
			if(parent instanceof Tag)	{
				tag=(Tag) parent;
				if(tag.getFullname().equalsIgnoreCase(name))
					return tag;
			}
			else if(parent instanceof TryCatchFinally)
				return parent;
		}
	}

}