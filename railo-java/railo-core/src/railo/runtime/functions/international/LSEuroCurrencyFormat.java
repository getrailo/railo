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
 * Implements the CFML Function lseurocurrencyformat
 */
package railo.runtime.functions.international;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class LSEuroCurrencyFormat implements Function { 

	private static final long serialVersionUID = -9214893090412056842L;
	public static String call(PageContext pc , Object number) throws PageException {
		return LSCurrencyFormat.call(pc,number);
	}
	public static String call(PageContext pc , Object number, String type) throws PageException {
		return LSCurrencyFormat.call(pc,number,type);
	}
	public static String call(PageContext pc , Object number, String type,String locale) throws PageException {
		return LSCurrencyFormat.call(pc,number,type,locale);
	}
}