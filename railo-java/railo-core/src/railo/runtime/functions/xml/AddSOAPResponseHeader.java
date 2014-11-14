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

import org.apache.axis.AxisFault;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.AxisUtil;
import railo.runtime.op.Caster;

public class AddSOAPResponseHeader {
	public static boolean call(PageContext pc, String nameSpace, String name, Object value) throws PageException {
		return call(pc, nameSpace, name, value,false);
	}
	public static boolean call(PageContext pc, String nameSpace, String name, Object value, boolean mustUnderstand) throws PageException {
		try {
			AxisUtil.addSOAPResponseHeader(nameSpace, name, value, mustUnderstand);
		} 
		catch (AxisFault e) {
			throw Caster.toPageException(e);
		}
		return true;
	}
}
