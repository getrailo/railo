/**
 * Implements the Cold Fusion Function arraytolist
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public final class ArrayToList implements Function {
	public static String call(PageContext pc , Array array) throws PageException {
		return call(pc,array,',');
	}
	public static String call(PageContext pc , Array array, String delimeter) throws PageException {
		if(delimeter.length()==1) return call(pc,array,delimeter.charAt(0));
		int len=array.size();
		if(len==0) return "";
		if(len==1)return Caster.toString(array.getE(1));
		
		Object o=array.get(1,null);
		StringBuffer sb=new StringBuffer(o==null?"":Caster.toString(o));
		for(int i=2;i<=len;i++) {
			sb.append(delimeter);
			o=array.get(i,null);
			sb.append(o==null?"":Caster.toString(o));
		}
		return sb.toString();
	}
	public static String call(PageContext pc , Array array, char delimeter) throws PageException {
		int len=array.size();
		if(len==0) return "";
		if(len==1)return Caster.toString(array.getE(1));
		
		Object o=array.get(1,null);
		StringBuffer sb=new StringBuffer(o==null?"":Caster.toString(o));
		for(int i=2;i<=len;i++) {
			sb.append(delimeter);
			o=array.get(i,null);
			sb.append(o==null?"":Caster.toString(o));
		}
		return sb.toString();
	}
}