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
/**
 * Implements the CFML Function dollarformat
 */
package railo.runtime.functions.displayFormatting;

import java.util.Locale;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.international.LSCurrencyFormat;
import railo.runtime.op.Caster;

public final class DollarFormat implements Function {
	public static String call(PageContext pc , String strDollar) throws CasterException {
		if(StringUtil.isEmpty(strDollar)) strDollar="0";//"$0,00";
		return LSCurrencyFormat.local(Locale.US, Caster.toDoubleValue(strDollar));
		/*try {
			return "$"+Caster.toDecimal(Caster.toDoubleValue(strDollar),',','.');
		} catch (PageException e) {
			throw new FunctionException(pc,"dollarFormat",1,"number",e.getMessage());
		}*/
	}
}