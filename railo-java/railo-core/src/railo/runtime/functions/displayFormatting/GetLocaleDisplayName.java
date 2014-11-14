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
 * Implements the CFML Function formatbasen
 */
package railo.runtime.functions.displayFormatting;

import java.util.Locale;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class GetLocaleDisplayName implements Function {
	public static String call(PageContext pc) {
		return _call(pc.getLocale(), pc.getLocale());
	}
	
	public static String call(PageContext pc , String locale) throws ExpressionException {
		Locale l = Caster.toLocale(locale);
		return _call(l, l);
	}
	
	public static String call(PageContext pc , String locale, String dspLocale) throws ExpressionException {
		if(StringUtil.isEmpty(dspLocale))dspLocale=locale;
		return _call(Caster.toLocale(locale), Caster.toLocale(dspLocale));
	}
	
	private static String _call(Locale locale, Locale dspLocale) {
		return locale.getDisplayName(dspLocale);
	}

	
	
}