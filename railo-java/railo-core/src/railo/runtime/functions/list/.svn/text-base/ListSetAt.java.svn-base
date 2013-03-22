/**
 * Implements the Cold Fusion Function listsetat
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.List;

public final class ListSetAt implements Function {
	public static String call(PageContext pc , String list, double pos, String value) throws ExpressionException {
		return call(pc,list,pos,value,",");
	}
	/*public static String call(PageContext pc , String list, double pos, String value, String delimeter) throws ExpressionException {
		
		if(pos<1) 
			throw new ExpressionException("invalid argument for function listSetAt, second argument [position] must be greater than 0, now ["+pos+"]");
		if(list.length()==0)
			throw new ExpressionException("invalid argument for function listSetAt, first argument list can't be empty");
		
		int[] removedInfo=new int[2];
		String[] arr=List.listToArray(List.trim(list,delimeter,removedInfo),delimeter);

		if(pos>arr.length)
			throw new ExpressionException("invalid argument for function listSetAt, second argument [position] must be greater than 0 and at least "+arr.length+", now ["+pos+"]");
		pos--;
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<arr.length;i++) {
			if(i!=0)sb.append(delimeter);
			if(i==pos)sb.append(value);
			else sb.append(arr[i]);
		}
		//return sb.toString();
		return RepeatString.call(pc,delimeter,removedInfo[0])+sb.toString()+RepeatString.call(pc,delimeter,removedInfo[1]);
		
	}*/

	public static String call(PageContext pc , String list, double posNumber, String value, String delimeter) throws ExpressionException {
		
		if(list.length()==0)
			throw new FunctionException(pc,"listSetAt",1,"list","can't be empty");
		
		
		int pos=((int) posNumber);
		//int[] removedInfo=new int[2];
		
		Array arr = List.listToArray(list,delimeter);
		int len=arr.size();
		
		// invalid index
		if(pos<1)
			throw new FunctionException(pc,"listSetAt",2,"position","invalid string list index ["+(pos)+"]");
		else if(len<pos) {
			throw new FunctionException(pc,"listSetAt",2,"position","invalid string list index ["+(pos)+"], indexes go from 1 to "+(len));
		}
		
		StringBuffer sb=new StringBuffer();//RepeatString.call(new StringBuffer(),delimeter,removedInfo[0]);
		boolean hasStart=false;
		boolean hasSet=false;
		String v;
		int count=0;
		for(int i=1;i<=len;i++) {
			v=(String)arr.get(i,"");
			if(hasStart) {
				sb.append(delimeter);
			}
			else hasStart=true;
			
			if(v.length()>0)count++;
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