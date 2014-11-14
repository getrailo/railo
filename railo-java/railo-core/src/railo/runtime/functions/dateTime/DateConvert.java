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
 * Implements the CFML Function dateconvert
 */
package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class DateConvert implements Function {
	public static DateTime call(PageContext pc , String conversionType, DateTime date) throws ExpressionException {
		int offset = pc.getTimeZone().getOffset(date.getTime());
		conversionType=conversionType.toLowerCase();
		
		if(conversionType.equals("local2utc")) {
			return new DateTimeImpl(pc,date.getTime()-offset,false);
		}
		else if(conversionType.equals("utc2local")) {
			return new DateTimeImpl(pc,date.getTime()+offset,false);
		}		
		throw new ExpressionException("invalid conversion-type ["+conversionType+"] for function dateConvert");
	}
}