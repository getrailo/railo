/**
 * Implements the CFML Function structkeylist
 */
package railo.runtime.functions.struct;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;
import railo.runtime.type.Collection.Key;

public final class StructKeyList implements Function {
	public static String call(PageContext pc , Struct struct) {
		return call(pc,struct,",");//KeyImpl.toUpperCaseList(struct.keys(), ",");
	}
	public static String call(PageContext pc , Struct struct, String delimiter) {
		//return KeyImpl.toList(CollectionUtil.keys(struct), delimiter);
		
		if(struct==null) return "";
		Iterator<Key> it = struct.keyIterator();
		
		// first
		if(!it.hasNext()) return "";
		StringBuilder sb=new StringBuilder();
		sb.append(it.next().getString());
		
		// rest
		if(delimiter.length()==1) {
			char c=delimiter.charAt(0);
			while(it.hasNext()){
				sb.append(c);
				sb.append(it.next().getString());
			}
		}
		else {
			while(it.hasNext()){
				sb.append(delimiter);
				sb.append(it.next().getString());
			}
		}
		
		return sb.toString();
	}
}