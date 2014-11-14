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
 * Implements the CFML Function urldecode
 */
package railo.runtime.functions.other;

import java.io.UnsupportedEncodingException;

import railo.commons.net.URLDecoder;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class URLDecode implements Function {
	public static String call(PageContext pc , String str) throws ExpressionException {
		return call(pc,str,"utf-8");
	}
	public static String call(PageContext pc , String str, String encoding) throws ExpressionException {
		try {
			return java.net.URLDecoder.decode(str,encoding);
		} catch (Throwable t) {
			try {
				return URLDecoder.decode(str,encoding,true);
			} catch (UnsupportedEncodingException uee) {
				throw new ExpressionException(uee.getMessage());
			}
		}
		/*try {
			return URLDecoder.decode(str,encoding);
		} catch (UnsupportedEncodingException e) {
			throw new ExpressionException(e.getMessage());
		}*/
	}
}