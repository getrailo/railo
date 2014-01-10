package railo.runtime.functions.other;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.converter.LazyConverter;
import railo.runtime.op.Operator;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.UDF;

public class ObjectEquals {
	public static boolean call(PageContext pc , Object left, Object right) {
		return Operator.equalsComplexEL(left, right, false, false);
		//return _equals(new HashSet<Object>(), left, right);
	}
	
	private static boolean _equals(HashSet<Object> done,Object left, Object right) {
		// null
		if(left==null) {
			return right==null;
		}
		if(left==right) return true;
		
		Object rawLeft = LazyConverter.toRaw(left);
		Object rawRight = LazyConverter.toRaw(right);
		
		if(done.contains(rawLeft)) return done.contains(rawRight);
		done.add(rawLeft);
		done.add(rawRight);
		try{
		
			// Components
			if(left instanceof Component){
				if(!(right instanceof Component)) return false;
				return _equals(done,(Component)left, (Component)right);
			}
			
			// Collection
			if(left instanceof Collection){
				if(!(right instanceof Collection)) return false;
				return _equals(done,(Collection)left, (Collection)right);
			}
			
			if(left instanceof UDF) {
				if(!(right instanceof UDF)) return false;
				
				
			}
			
			// other
			return left.equals(right);
		}
		finally {
			done.remove(rawLeft);
			done.remove(rawRight);
		}
	}
	
	private static boolean _equals(HashSet<Object> done,Collection left, Collection right) {
		if(left.size()!=right.size()) return false;
		Iterator<Entry<Key, Object>> it = left.entryIterator();
		Entry<Key, Object> e;
		Object l,r;
		while(it.hasNext()){
			e = it.next();
			l=e.getValue();
			r=right.get(e.getKey(),null);
			if(r==null || !_equals(done,l, r)) return false;
		}
		return true;
	}
	
	private static boolean _equals(HashSet<Object> done,Component left, Component right) {
		if(left==null || right==null) return false;
		
		if(!left.getPageSource().equals(right.getPageSource())) return false;
		
		if(!_equals(done,left.getComponentScope(),right.getComponentScope())) return false;
		
		if(!_equals(done,(Collection)left,(Collection)right)) return false;

		return true;
	}
}
