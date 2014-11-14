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
package railo.runtime.functions.xml;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.rpc.AxisUtil;
import railo.runtime.net.rpc.client.WSClient;
import railo.runtime.op.Caster;

/**
 * 
 */
public final class GetSOAPRequest implements Function {

	private static final long serialVersionUID = -1743528432690118148L;

	public static Object call(PageContext pc) throws PageException {
		return call(pc, null);
	}
	public static Object call(PageContext pc, Object webservice) throws PageException {
		if(webservice!=null && !(webservice instanceof WSClient))
			throw new FunctionException(pc, "getSOAPRequest", 1, "webservice", "value must be a webservice Object generated with createObject/<cfobject>");
		
		try {
			return AxisUtil.getSOAPRequest((WSClient) webservice);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
	}
}