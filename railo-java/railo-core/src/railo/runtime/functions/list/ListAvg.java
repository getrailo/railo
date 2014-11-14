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
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ListUtil;


public final class ListAvg extends BIF {

	private static final long serialVersionUID = -7365055491706152507L;

	public static double call(PageContext pc , String list) throws ExpressionException {
        return call(pc,list,",",false);
    }
    public static double call(PageContext pc , String list, String delimiter) throws ExpressionException {
        return call(pc, list, delimiter, false);
    }
    
    public static double call(PageContext pc , String list, String delimiter, boolean multiCharacterDelimiter) throws ExpressionException {
        return ArrayUtil.avg(ListUtil.listToArray(list, delimiter,false, multiCharacterDelimiter));
    }

    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==1)
			return call(pc, Caster.toString(args[0]));
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toString(args[1]), Caster.toBooleanValue(args[2]));
    	
		throw new FunctionException(pc, "ListAvg", 1, 3, args.length);
	}
}