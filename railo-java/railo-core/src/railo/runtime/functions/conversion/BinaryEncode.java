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
package railo.runtime.functions.conversion;

import railo.runtime.PageContext;
import railo.runtime.coder.Coder;
import railo.runtime.coder.CoderException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class BinaryEncode implements Function {
	
	/**
	 * @param pc
	 * @param binary
	 * @param binaryencoding
	 * @return encoded string
	 * @throws PageException
	 */
	public static String call(PageContext pc, byte[] binary, String binaryencoding) throws PageException {
		try {
			return Coder.encode(binaryencoding,binary);
		} catch (CoderException e) {
			throw Caster.toPageException(e);
		}
	}
}