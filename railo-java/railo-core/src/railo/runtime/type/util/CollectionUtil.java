package railo.runtime.type.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.runtime.op.Operator;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

public class CollectionUtil {

	private static final Object NULL = new Object();

	
	public static boolean equals(Collection left, Collection right) {
		if(left.size()!=right.size()) return false;
		Iterator<Key> it = left.keyIterator();
		Key k;
		Object l,r;
		while(it.hasNext()){
			k=it.next();
			r=right.get(k,NULL);
			if(r==NULL) return false;
			l=left.get(k,NULL);
			if(!Operator.equalsEL(r, l, false, true)) return false;
		}
		return true;
	}

	/*public static String[] toStringArray(Key[] keys) {
		if(keys==null) return null;
		String[] arr=new String[keys.length];
		for(int i=0;i<keys.length;i++){
			arr[i]=keys[i].getString();
		}
		return arr;
	}*/
	
	public static String getKeyList(Iterator<Key> it, String delimiter) {
		StringBuilder sb=new StringBuilder(it.next().getString());
		if(delimiter.length()==1) {
			char c=delimiter.charAt(0);
			while(it.hasNext()) {
				sb.append(c);
				sb.append(it.next().getString());
			}
		}
		else {
			while(it.hasNext()) {
				sb.append(delimiter);
				sb.append(it.next().getString());
			}
		}
		

		return sb.toString();
	}
	

	public static String getKeyList(Collection coll, String delimiter) {
		if(coll.size()==0) return "";
		return getKeyList(coll.keyIterator(), delimiter);
	}

	public static Key[] keys(Collection coll) { 
		if(coll==null) return new Key[0];
		Iterator<Key> it = coll.keyIterator();
		List<Key> rtn=new ArrayList<Key>();
		if(it!=null)while(it.hasNext()){
			rtn.add(it.next());
		}
		return rtn.toArray(new Key[rtn.size()]);
	}
	
	public static String[] keysAsString(Collection coll) {
		if(coll==null) return new String[0];
		Iterator<Key> it = coll.keyIterator();
		List<String> rtn=new ArrayList<String>();
		if(it!=null)while(it.hasNext()){
			rtn.add(it.next().getString());
		}
		return rtn.toArray(new String[rtn.size()]);
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map==null || map.size()==0;
	}

	/*public static int hashCode(Collection coll) { produce infiniti loop when there is a refrerence to itself or a anchestor

		int hashCode = 1;
		Iterator<Entry<Key, Object>> it = coll.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()) {
			e = it.next();
			hashCode = 31*hashCode+
			
			(
					 (e.getKey()==null?0:e.getKey().hashCode()) ^
					  (e.getValue()==null ? 0 : e.getValue().hashCode())		
			);
		}
		return hashCode;
	}*/
}
