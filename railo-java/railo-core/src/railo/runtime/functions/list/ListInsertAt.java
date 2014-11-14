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
 * Implements the CFML Function listinsertat
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public final class ListInsertAt extends BIF {
	
	private static final long serialVersionUID = 2796195727971683118L;

	public static String call(PageContext pc , String list, double posNumber, String value) throws ExpressionException {
		return call(pc,list,posNumber,value,",",false);
	}
	
	public static String call(PageContext pc , String list, double posNumber, String value, String strDelimiter) throws ExpressionException {
		return call(pc,list,posNumber,value,strDelimiter,false);
	}
		
	public static String call(PageContext pc , String list, double posNumber, String value, String strDelimiter, boolean includeEmptyFields) throws ExpressionException {
		if(strDelimiter.length()==0)
        throw new FunctionException(pc,"listInsertAt",4,"delimiter","invalid delimiter value, can't be a empty string"); 
        
        return ListUtil.listInsertAt(list,(int)posNumber,value,strDelimiter,!includeEmptyFields);
	}
    
    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toString(args[2]), Caster.toString(args[3]));
    	if(args.length==5)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toString(args[2]), Caster.toString(args[3]), Caster.toBooleanValue(args[4]));
    	
		throw new FunctionException(pc, "ListInsertAt", 3, 5, args.length);
	}
}