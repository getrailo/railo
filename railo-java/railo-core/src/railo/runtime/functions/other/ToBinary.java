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
 * Implements the CFML Function tobinary
 */
package railo.runtime.functions.other;

import java.nio.charset.Charset;

import railo.commons.io.CharsetUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class ToBinary implements Function {

	private static final long serialVersionUID = 4541724601337401920L;

	public static byte[] call(PageContext pc , Object data) throws PageException {
		return call(pc, data, null);
	}
	public static byte[] call(PageContext pc , Object data, String charset) throws PageException {
		if(!StringUtil.isEmpty(charset)) {
			charset=charset.trim().toLowerCase();
			Charset cs;
			if("web".equalsIgnoreCase(charset))cs=((PageContextImpl)pc).getWebCharset();
			if("resource".equalsIgnoreCase(charset))cs=((PageContextImpl)pc).getResourceCharset();
			else cs=CharsetUtil.toCharset(charset);
				
			String str=Caster.toString(data);
			return str.getBytes(cs);
		}
		return Caster.toBinary(data);
	}
}