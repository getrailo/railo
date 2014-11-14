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
 * Implements the CFML Function createtimespan
 */
package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.dt.TimeSpanImpl;

public final class CreateTimeSpan implements Function {
	public static TimeSpan call(PageContext pc , double day, double hour, double minute, double second) {
		return new TimeSpanImpl((int)day,(int)hour,(int)minute,(int)second);
	}
	
	public static TimeSpan call(PageContext pc , double day, double hour, double minute, double second,double millisecond) {
		return new TimeSpanImpl((int)day,(int)hour,(int)minute,(int)second,(int)millisecond);
	}
}