package railo.runtime.type.util;

import java.util.Iterator;

import railo.runtime.op.Operator;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

public class CollectionUtil {

	private static final Object NULL = new Object();

	
	public static boolean equals(Collection left, Collection right) {
		if(left.size()!=right.size()) return false;
		
		Key[] keys = left.keys();
		Object l,r;
		for(int i=0;i<keys.length;i++){
			r=right.get(keys[i],NULL);
			if(r==NULL) return false;
			l=left.get(keys[i],NULL);
			if(!Operator.equalsEL(r, l, false, true)) return false;
		}
		return true;
	}
	
	public static Iterator toIterator(Collection c){
		return c.iterator();
	}

	public static String[] toStringArray(Key[] keys) {
		if(keys==null) return null;
		String[] arr=new String[keys.length];
		for(int i=0;i<keys.length;i++){
			arr[i]=keys[i].getString();
		}
		return arr;
	}

	/*public static String keyList(Collection coll, String delimeter) {
		return List.arrayToList(coll.keys(),delimeter);
	}*/

	/*public static String keyList(Collection.Key[] keys, String delimeter) {
		return List.arrayToList(keys,delimeter);
	}*/

}
