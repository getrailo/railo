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
 * Implements the CFML Function listlast
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.tag.util.DeprecatedUtil;

public final class ListTrim extends BIF {
	
	private static final long serialVersionUID = 2354456835027080741L;
	
	public static String call(PageContext pc , String list) {
		DeprecatedUtil.function(pc,"ListTrim","ListCompact");
		return ListCompact.call(pc,list,",");
	}
	public static String call(PageContext pc , String list, String delimiter) {
		DeprecatedUtil.function(pc,"ListTrim","ListCompact");
		return ListCompact.call(pc,list,delimiter);
	}
	
    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	
		throw new FunctionException(pc, "ListTrim", 1, 2, args.length);
	}
}