package railo.runtime.type.util;

import railo.runtime.op.Operator;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

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

}
