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
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Reduce;
import railo.runtime.op.Caster;
import railo.runtime.type.UDF;
import railo.runtime.type.util.StringListData;


public final class ListReduce extends BIF {

	private static final long serialVersionUID = 1857478124366819325L;

	public static Object call(PageContext pc , String list, UDF udf) throws PageException {
		return call(pc, list, udf, null, ",", false,true);
	}
	
	public static Object call(PageContext pc , String list, UDF udf, Object initValue) throws PageException {
		return call(pc, list, udf, initValue, ",", false,true);
	}
	
	public static Object call(PageContext pc , String list, UDF udf, Object initValue ,String delimiter) throws PageException {
		return call(pc, list, udf, initValue, delimiter, false,true);
	}

	public static Object call(PageContext pc , String list, UDF udf, Object initValue ,String delimiter
			, boolean includeEmptyFields) throws PageException {
		return call(pc, list, udf, initValue, delimiter, includeEmptyFields, true);
	}
	public static Object call(PageContext pc , String list, UDF udf, Object initValue ,String delimiter
			, boolean includeEmptyFields, boolean multiCharacterDelimiter) throws PageException {
		StringListData data=new StringListData(list,delimiter,includeEmptyFields,multiCharacterDelimiter);
		
		return Reduce._call(pc, data, udf,initValue);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),args[2]);
		if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),args[2],Caster.toString(args[3]));
		if(args.length==5)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),args[2],Caster.toString(args[3]),Caster.toBooleanValue(args[4]));
		if(args.length==6)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),args[2],Caster.toString(args[3]),Caster.toBooleanValue(args[4]),Caster.toBooleanValue(args[5]));
		
		throw new FunctionException(pc, "ListReduce", 2, 6, args.length);
	}
}