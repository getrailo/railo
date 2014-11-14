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
 * Implements the CFML Function chr
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Chr implements Function {

	private static final long serialVersionUID = -4941617303146860984L;

	public static String call(PageContext pc , double number) throws ExpressionException {
		int value=(int) number;
		if(value<1){
			if(value==0)return "";
			//else {
				throw new ExpressionException("Parameter 1 of function chr which is now ["+value+"] must be a non-negative integer");
			//}
		}
		return ""+(char)value;		
	}
}