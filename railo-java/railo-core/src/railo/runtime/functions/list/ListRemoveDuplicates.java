/**
 * Implements the Cold Fusion Function listrest
 */
package railo.runtime.functions.list;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.List;

public final class ListRemoveDuplicates implements Function {
	
	private static final long serialVersionUID = -6596215135126751629L;
	
	public static String call(PageContext pc , String list) throws PageException {
		return call(pc,list, ",");
	}
	public static String call(PageContext pc , String list, String delimiter) throws PageException {
		if(delimiter==null) delimiter=",";
		Array array = List.listToArrayRemoveEmpty(list, delimiter);
		Set<String> existing=new HashSet<String>();
		StringBuilder sb=new StringBuilder();
		//Key[] keys = array.keys();
		Iterator<Object> it = array.valueIterator();
		String value;
		while(it.hasNext()){
			value=Caster.toString(it.next());
			if(existing.contains(value)) continue;
			
			existing.add(value);
			if(sb.length()>0) sb.append(delimiter);
			sb.append(value);
			
		}
		return sb.toString();
	}
}