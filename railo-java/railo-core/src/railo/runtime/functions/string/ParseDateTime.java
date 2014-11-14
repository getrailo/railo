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
package railo.runtime.functions.string;

import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.date.DateCaster;

/**
 * Implements the CFML Function parsedatetime
 */
public final class ParseDateTime implements Function {

	private static final long serialVersionUID = -2623323893206022437L;
	
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate) throws PageException {
		return _call(oDate,pc.getTimeZone());
	}
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate, String popConversion) throws PageException {
		return _call(oDate,pc.getTimeZone());
	}
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate, String popConversion,String strTimezone) throws PageException {
		return _call(oDate,strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	private static railo.runtime.type.dt.DateTime _call( Object oDate,TimeZone tz) throws PageException {
		return DateCaster.toDateAdvanced(oDate,DateCaster.CONVERTING_TYPE_YEAR,tz);
	}
}