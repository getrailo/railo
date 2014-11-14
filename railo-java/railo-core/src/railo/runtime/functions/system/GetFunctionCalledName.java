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

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.UDF;

/**
 * returns the root of this actuell Page Context
 */
public final class GetFunctionCalledName implements Function {

	private static final long serialVersionUID = -3345605395096765821L;

	public static String call(PageContext pc) {
		PageContextImpl pci=(PageContextImpl) pc;
		Key name = pci.getActiveUDFCalledName();
		if(name!=null) return name.getString();
		
		UDF[] udfs = ((PageContextImpl)pc).getUDFs();
		if(udfs.length==0) return "";
		return udfs[udfs.length-1].getFunctionName();
	}
}