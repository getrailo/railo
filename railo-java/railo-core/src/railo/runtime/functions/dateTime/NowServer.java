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
package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * Implements the CFML Function now
 * @deprecated removed with no replacement
 */
public final class NowServer implements Function {
	/**
	 * @param pc
	 * @return server time
	 * @throws ExpressionException 
	 */
	public static DateTime call(PageContext pc ) throws ExpressionException {
		DeprecatedUtil.function(pc,"nowServer");
		long now = System.currentTimeMillis();
		int railo = pc.getTimeZone().getOffset(now);
		int server = TimeZone.getDefault().getOffset(now);
		
		return new DateTimeImpl(pc,now-(railo-server),false);
		
	}
	
}