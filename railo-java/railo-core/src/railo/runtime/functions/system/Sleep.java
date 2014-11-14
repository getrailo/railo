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
package railo.runtime.functions.system;

import railo.commons.io.SystemUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;

public class Sleep {

	public static String call(PageContext pc, double duration) throws FunctionException {
		if(duration>=0) {
			SystemUtil.sleep((long)duration);
		}
		else throw new FunctionException(pc,"sleep",1,"duration","attribute interval must be greater or equal to 0, now ["+(duration)+"]");
		return null;
		
	}
}
