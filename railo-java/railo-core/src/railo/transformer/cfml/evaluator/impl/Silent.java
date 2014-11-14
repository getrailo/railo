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

import org.w3c.dom.Element;

import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;

/**
 * Prueft den Kontext des Tag <code>try</code>.
 * Innerhalb des Tag try muss sich am Schluss 1 bis n Tags vom Typ catch befinden.
 */
public final class Silent extends EvaluatorSupport {
	
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(Element)
	 */
	public void evaluate(Tag tag) throws EvaluatorException {
		ASMUtil.removeLiterlChildren(tag,true);
	}
}