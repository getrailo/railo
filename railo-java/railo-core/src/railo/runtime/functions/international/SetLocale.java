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
package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

/**
 * Implements the CFML Function setlocale
 */
public final class SetLocale implements Function {
	
	private static final long serialVersionUID = -4941933470300726563L;

	public static String call(PageContext pc , String strLocale) throws PageException {
	       	Locale old=pc.getLocale();
	       	pc.setLocale(LocaleFactory.getLocale(strLocale));
	       	return LocaleFactory.toString(old);
			
	}
}