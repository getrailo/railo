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
 * Implements the CFML Function bitshrn
 */
package railo.runtime.functions.math;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class BitSHRN implements Function {
	
	public static double call(PageContext pc , double dnumber, double dcount) throws FunctionException {
		int number=(int) dnumber,count=(int) dcount;
		if(count > 31 || count < 0)
			throw new FunctionException(pc,"bitSHRN",2,"count","must be beetween 0 and 31 now "+count);
		
		return number >>> count;
	}	
}