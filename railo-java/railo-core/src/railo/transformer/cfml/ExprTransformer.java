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
package railo.transformer.cfml;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 * Innerhalb einer TLD (Tag Library Descriptor) kann eine Klasse angemeldet werden, 
 * welche das Interface ExprTransfomer implementiert, 
 * um Ausdruecke die innerhalb von Attributen und dem Body von Tags vorkommen zu transformieren. 
 * Die Idee dieses Interface ist es die Moeglichkeit zu bieten, 
 * weitere ExprTransfomer zu erstellen zu koennen, 
 * um fuer verschiedene TLD, verschiedene Ausdrucksarten zu bieten. 
 *
 */
public interface ExprTransformer {

	/**
	* Wird aufgerufen um aus dem uebergebenen CFMLString einen Ausdruck auszulesen 
	 * und diesen in ein CFXD Element zu uebersetzten.
	 * <br>
	 * Beispiel eines uebergebenen String:<br>
	 * "session.firstName" oder "trim(left('test'&var1,3))"
	 * 
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des CFML Codes erkennen 
	 * und validieren.
	 * @param doc XML Document des aktuellen zu erstellenden CFXD
	 * @param cfml Text der transfomiert werden soll.
	 * @return Element CFXD Element
	 * @throws railo.runtime.exp.TemplateException 
	 * @throws TemplateException
	 */
	public Expression transform(Page page,EvaluatorPool ep,TagLib[][] tld, FunctionLib[] fld,TagLibTag[] scriptTags,CFMLString cfml, TransfomerSettings settings) throws TemplateException;
	
	/**
	* Wird aufgerufen um aus dem uebergebenen CFMLString einen Ausdruck auszulesen 
	 * und diesen in ein CFXD Element zu uebersetzten. Es wird aber davon ausgegangen das es sich um einen String handelt.
	 * <br>
	 * Beispiel eines uebergebenen String:<br>
	 * "session.firstName" oder "trim(left('test'&var1,3))"
	 * 
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des CFML Codes erkennen 
	 * und validieren.
	 * @param doc XML Document des aktuellen zu erstellenden CFXD
	 * @param cfml Text der transfomiert werden soll.
	 * @return Element CFXD Element
	 * @throws TemplateException
	 */
	public Expression transformAsString(Page page,EvaluatorPool ep,TagLib[][] tld, FunctionLib[] fld,TagLibTag[] scriptTags,CFMLString cfml, TransfomerSettings settings,boolean allowLowerThan) throws TemplateException;
}