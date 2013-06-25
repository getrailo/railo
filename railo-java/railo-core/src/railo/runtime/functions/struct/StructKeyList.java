/**
 * Implements the CFML Function structkeylist
 */
package railo.runtime.functions.struct;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;

public final class StructKeyList extends BIF {
	
	private static final long serialVersionUID = 6256709521354910213L;

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
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2) return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]));
		return call(pc,Caster.toStruct(args[0]));
	}
}