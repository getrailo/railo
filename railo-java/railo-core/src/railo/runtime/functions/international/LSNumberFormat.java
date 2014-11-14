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
/** DIFF 23
 * Implements the CFML Function lsnumberformat
 */
package railo.runtime.functions.international;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.displayFormatting.NumberFormat;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.util.InvalidMaskException;

public final class LSNumberFormat implements Function {
	
	private static final long serialVersionUID = -7981883050285346336L;

	public static String call(PageContext pc , Object object) throws PageException {
		return call(pc, object, null, pc.getLocale());
	}
	
	public static String call(PageContext pc , Object object, String mask) throws PageException {
	    return call(pc, object, mask, pc.getLocale());
	}
	
	public static String call(PageContext pc , Object object, String mask, String locale) throws PageException {
		return call(pc, object, mask, 
				locale==null?pc.getLocale():LocaleFactory.getLocale(locale));
	}
	
	private static String call(PageContext pc , Object object, String mask, Locale locale) throws PageException {
		try {
            if(mask==null) 
            	return new railo.runtime.util.NumberFormat().format(locale,NumberFormat.toNumber(pc,object));
			return new railo.runtime.util.NumberFormat().format(locale,NumberFormat.toNumber(pc,object),mask);
        } 
        catch (InvalidMaskException e) {
            throw new FunctionException(pc,"lsnumberFormat",1,"number",e.getMessage());
        }
	}
}