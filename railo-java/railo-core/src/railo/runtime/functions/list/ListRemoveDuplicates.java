/**
 * Implements the Cold Fusion Function listrest
 */
package railo.runtime.functions.list;

import java.util.HashSet;
import java.util.Set;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.List;

public final class ListRemoveDuplicates implements Function {
	
	private static final long serialVersionUID = -6596215135126751629L;
	
	public static String call(PageContext pc , String list) throws PageException {
		return call(pc,list, ",");
	}
	public static String call(PageContext pc , String list, String delimeter) throws PageException {
		if(delimeter==null) delimeter=",";
		Array array = List.listToArrayRemoveEmpty(list, delimeter);
		Set<String> existing=new HashSet<String>();
		StringBuilder sb=new StringBuilder();
		Key[] keys = array.keys();
		String value;
		for(int i=0;i<keys.length;i++){
			value=Caster.toString(array.get(keys[i]));
			if(existing.contains(value)) continue;
			
			existing.add(value);
			if(sb.length()>0) sb.append(delimeter);
			sb.append(value);
			
		}
		return sb.toString();
	}
}