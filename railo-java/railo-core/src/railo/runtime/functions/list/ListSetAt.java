/**
 * Implements the CFML Function listsetat
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

public final class ListSetAt implements Function {

	private static final long serialVersionUID = -105782799713547552L;

	public static String call(PageContext pc , String list, double posNumber, String value) throws ExpressionException {
		return call(pc,list,posNumber,value,",",false);
	}

	public static String call(PageContext pc , String list, double posNumber, String value, String delimiter) throws ExpressionException {
		return call(pc,list,posNumber,value,delimiter,false);
	}
	public static String call(PageContext pc , String list, double posNumber, String value, String delimiter, boolean includeEmptyFields) throws ExpressionException {
		
		if(list.length()==0)
			throw new FunctionException(pc,"listSetAt",1,"list","can't be empty");
		
		
		int pos=((int) posNumber);
		//int[] removedInfo=new int[2];
		
		Array arr = ListUtil.listToArray(list,delimiter);
		int len=arr.size();
		
		// invalid index
		if(pos<1)
			throw new FunctionException(pc,"listSetAt",2,"position","invalid string list index ["+(pos)+"]");
		else if(len<pos) {
			throw new FunctionException(pc,"listSetAt",2,"position","invalid string list index ["+(pos)+"], indexes go from 1 to "+(len));
		}
		
		StringBuffer sb=new StringBuffer();//RepeatString.call(new StringBuffer(),delimiter,removedInfo[0]);
		boolean hasStart=false;
		boolean hasSet=false;
		String v;
		int count=0;
		for(int i=1;i<=len;i++) {
			v=(String)arr.get(i,"");
			if(hasStart) {
				sb.append(delimiter);
			}
			else hasStart=true;
			
			if(includeEmptyFields || v.length()>0)count++;
			if(!hasSet && pos==count) {
				sb.append(value);
				hasSet=true;
			}
			else sb.append(arr.get(i,""));
		}
		if(!hasSet){
			throw new FunctionException(pc,"listSetAt",2,"position","invalid string list index ["+(pos)+"]");
		}
		
		
		return sb.toString();
	}
}